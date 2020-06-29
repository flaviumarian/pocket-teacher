package com.licence.pocketteacher.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Post;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.home.FragmentHomeS;
import com.licence.pocketteacher.student.profile.FragmentProfilesS;
import com.licence.pocketteacher.student.search.FragmentSearchS;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainPageS extends AppCompatActivity {

    private Dialog logOutPopup;
    private BottomNavigationView bottomNavigationView;
    public static BadgeDrawable badgeDrawable;


    public static ArrayList<Post> allPosts;
    public static boolean needsRefresh = false;

    public static Student student;
    public static int flag; // 0 - normal, 1 - facebook, 2 - google


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_s);

        getLoginIntent();
        initiateComponents();
        setListeners();


    }



    private void getLoginIntent() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            student = bundle.getParcelable("student");
            flag = bundle.getInt("flag");
        }
    }

    private void initiateComponents() {

        // Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new FragmentHomeS()).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setItemIconTintList(null);

        // Notification badge
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.profile);

        generateAllPosts();

        resetBadge();

    }

    private void setListeners() {

        // Bottom Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                boolean notConnected = false;

                switch (item.getItemId()) {
                    case R.id.search:
                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            notConnected = true;
                            break;
                        }
                        selectedFragment = new FragmentSearchS();
                        resetBadge();
                        break;
                    case R.id.home:
                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            notConnected = true;
                            break;
                        }
                        selectedFragment = new FragmentHomeS();
                        resetBadge();
                        break;
                    case R.id.profile:
                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            notConnected = true;
                            break;
                        }
                        selectedFragment = new FragmentProfilesS();
                        resetBadge();
                        break;
                }

                if(!notConnected) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                }else{
                    return false;
                }

                return true; // select the item - false would mean not selecting it
            }
        });
    }

    public static void generateAllPosts(){

        allPosts = HelpingFunctions.getAllPosts(student.getUsername());

    }

    public static void resetBadge(){

        String numberNotifications = HelpingFunctions.getNumberOfNotifications(student.getUsername());
        if(numberNotifications.equals("0")){
            badgeDrawable.setVisible(false);
        }else{
            badgeDrawable.setNumber(Integer.parseInt(numberNotifications));
            badgeDrawable.setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof FragmentSearchS || fragment instanceof FragmentProfilesS || fragment instanceof FragmentHomeS) {
            logOutPopup = new Dialog(MainPageS.this);
            logOutPopup.setContentView(R.layout.popup_log_out);

            ImageView closePopupIV = logOutPopup.findViewById(R.id.closePopupIV);
            closePopupIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOutPopup.dismiss();
                }
            });

            // Remove dialog background
            logOutPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            logOutPopup.show();

            Button yesButton = logOutPopup.findViewById(R.id.yesBttn);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOutPopup.dismiss();
                    if (flag == 0) {
                        // Regular
                        SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
                        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                        sharedPreferencesEditor.putBoolean("loggedIn", false);
                        sharedPreferencesEditor.apply();
                    }

                    if (flag == 1) {
                        // Facebook
                        LoginManager.getInstance().logOut();
                    }
                    if (flag == 2) {
                        // Google
                        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                        googleSignInClient.signOut();
                    }

                    // Delete token for this device
                    deleteToken();

                    Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); // clear all activities but this one
                    startActivity(intent);
                    finish();
                }
            });
        }else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragmentContainer, new FragmentSearchS());
            transaction.addToBackStack("search_page");
            transaction.commit();
        }

    }

    private void deleteToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainPageS.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String currentToken = instanceIdResult.getToken();
                HelpingFunctions.deleteToken(currentToken, student.getUsername());
            }
        });
    }

}
