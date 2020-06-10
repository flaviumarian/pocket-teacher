package com.licence.pocketteacher.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.folder.FragmentFolderT;
import com.licence.pocketteacher.teacher.profile.FragmentProfileT;
import com.licence.pocketteacher.teacher.updates.FragmentUpdatesT;

import java.util.Collections;

public class MainPageT extends AppCompatActivity {

    private Dialog logOutPopup;
    private BottomNavigationView bottomNavigationView;
    public static BadgeDrawable badgeDrawable;

    public static Teacher teacher;
    public static int flag; // 0 - normal, 1 - facebook, 2 - google


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_t);

        getLoginIntent();
        initiateComponents();
        setListeners();

    }

    private void getLoginIntent() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            teacher = bundle.getParcelable("teacher");
            flag = bundle.getInt("flag");
        }

    }

    private void initiateComponents(){

        // Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new FragmentUpdatesT()).commit();
        bottomNavigationView.setSelectedItemId(R.id.updates);
        bottomNavigationView.setItemIconTintList(null);

        // Subjects ArrayList ordering
        Collections.sort(teacher.getSubjects());

        // Notifications badge
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.updates);

        resetBadge();
    }

    private void setListeners(){

        // Bottom Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch(item.getItemId()){
                    case R.id.folder:
                        selectedFragment = new FragmentFolderT();
                        resetBadge();
                        break;
                    case R.id.updates:
                        selectedFragment = new FragmentUpdatesT();
                        break;
                    case R.id.profile:
                        selectedFragment = new FragmentProfileT();
                        resetBadge();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

                return true; // select the item - false would mean not selecting it
            }
        });
    }

    public static void resetBadge(){
        String numberNotifications = HelpingFunctions.getNumberOfNotifications(teacher.getUsername());
        if(numberNotifications.equals("0")){
            badgeDrawable.setVisible(false);
        }else{
            badgeDrawable.setNumber(Integer.parseInt(numberNotifications));
            badgeDrawable.setVisible(true);
        }
    }







    // When trying to go back
    @Override
    public void onBackPressed() {

        logOutPopup = new Dialog(this);
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
                    LoginManager.getInstance().logOut();
                }
                if (flag == 2){
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
    }

    private void deleteToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainPageT.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String currentToken = instanceIdResult.getToken();
                HelpingFunctions.deleteToken(currentToken, teacher.getUsername());
            }
        });
    }
}
