package com.licence.pocketteacher.student.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

public class ChangeQuickLoginS extends AppCompatActivity {

    private ImageView backIV;
    private Switch autoLoginSW;
    private Button resetBttn;

    private String initialAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_quick_login_s);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // initial auto login
        initialAutoLogin = HelpingFunctions.getAutoLoginBasedOnUsername(MainPageS.student.getUsername());

        // Image View
        backIV = findViewById(R.id.backIV);


        // Switch
        autoLoginSW = findViewById(R.id.autoLoginSW);
        if(initialAutoLogin.equals("0")){
            // no auto login
            autoLoginSW.setChecked(false);
        }else{
            // auto login
            autoLoginSW.setChecked(true);
        }
        // Button
        resetBttn = findViewById(R.id.resetBttn);

    }

    private void setListeners(){

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Switch
        autoLoginSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!autoLoginSW.isChecked()){
                    // auto login
                    autoLoginSW.setChecked(false);
                } else{
                    // no auto login
                    autoLoginSW.setChecked(true);
                }
            }
        });

        // Button
        resetBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(initialAutoLogin.equals("0")){
                    // no auto login
                    autoLoginSW.setChecked(false);
                }else{
                    // auto login
                    autoLoginSW.setChecked(true);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newAutoLogin;

        if(autoLoginSW.isChecked()){
            newAutoLogin = "1";
        } else{
            newAutoLogin = "0";
        }


        if(!initialAutoLogin.equals(newAutoLogin)){
            HelpingFunctions.setAutoLoginBasedOnUsername(MainPageS.student.getUsername(), newAutoLogin);

            // REGULAR logout
            if(MainPageS.flag == 0){
                SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                if(newAutoLogin.equals("0")){
                    sharedPreferencesEditor.putBoolean("loggedIn", false);
                }else{
                    sharedPreferencesEditor.putBoolean("loggedIn", true);
                }
                sharedPreferencesEditor.apply();
            }

            // FACEBOOK logout
            if(MainPageS.flag == 1 && newAutoLogin.equals("0")){
                LoginManager.getInstance().logOut();
            }

            // GOOGLE logout
            if(MainPageS.flag == 2 && newAutoLogin.equals("0")){
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                googleSignInClient.signOut();
            }
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
