package com.licence.pocketteacher.student.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.initial_activities.ConfirmationSent;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

public class ChangeEmailS extends AppCompatActivity {

    private ImageView backIV;
    private EditText emailET;
    private Button resetBttn;
    private TextView warningTV;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_s);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // email
        email = MainPageS.student.getEmail();

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Text
        emailET = findViewById(R.id.emailET);
        emailET.setText(email);

        // Button
        resetBttn = findViewById(R.id.resetBttn);


        if(MainPageS.flag > 0){
            warningTV = findViewById(R.id.warningTV);
            warningTV.setVisibility(View.VISIBLE);
        }
    }

    private void setListeners(){

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Button
        resetBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailET.setText(email);
                emailET.setError(null);
                emailET.setSelection(emailET.length());
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }


        if(HelpingFunctions.isEditTextEmpty(emailET)){
            emailET.setError("Insert email.");
            return;
        }

        if(!HelpingFunctions.isEmailValid(emailET.getText().toString())){
            emailET.setError("Insert a valid email.");
            return;
        }

        String newEmail = emailET.getText().toString().toLowerCase();

        if(newEmail.equals(email)){
            finish();
            ChangeEmailS.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
            return;
        }

        if(!HelpingFunctions.getEmailBasedOnEmail(newEmail).equals("User not found.")){
            emailET.setError("Email already has an account associated to it.");
            return;
        }

        HelpingFunctions.setEmailBasedOnEmail(email, newEmail);
        HelpingFunctions.setStatusBasedOnEmail(newEmail, "0");

        MainPageS.student.setEmail(newEmail);


        if(MainPageS.flag == 1){
            // facebook logout
            LoginManager.getInstance().logOut();
        }
        if(MainPageS.flag == 2){
            // google logout
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
            googleSignInClient.signOut();
        }


        // Send the email
        String subject = "Email address change request - NO-REPLY";
        String confirmationCode = HelpingFunctions.generateRandomString(10);
        String text = "Dear Pocket Teacher user,\n\n\tA request to change your account's old email address to this one has been made. If you do not recognize this action, please ignore this email.\nIf you are aware of this, know that in order to validate your account and be able to continue using the app, you must use the following code when asked for it: \n\n\t\t\t" + confirmationCode;
        HelpingFunctions.setConfirmationCodeBasedOnEmail(newEmail, confirmationCode);
        HelpingFunctions.sendEmail(newEmail, subject, text);

        // Auto-login purposes
        SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("email", newEmail);
        sharedPreferencesEditor.apply();

        Intent intent = new Intent(this, ConfirmationSent.class);
        intent.putExtra("fromRegister", false);
        startActivity(intent);
        finish();
        ChangeEmailS.this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
    }

}
