package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;


public class ForgotPassword extends AppCompatActivity {

    private ImageView backIV;
    private TextView signUpTV;
    private EditText emailET;
    private Button sendBttn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initiateComponents();
        setOnClickListeners();

    }


    private void initiateComponents() {
        // Image View
        backIV = findViewById(R.id.backIV);

        // Text View
        signUpTV = findViewById(R.id.signUpTV);

        // Edit Text
        emailET = findViewById(R.id.emailET);

        // Button
        sendBttn = findViewById(R.id.sendBttn);

    }

    private void setOnClickListeners() {
        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Text View
        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                finish();
            }
        });

        // Button
        sendBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (HelpingFunctions.isEditTextEmpty(emailET)) {
                    emailET.setError("Insert email.");
                    return;
                } else if (!HelpingFunctions.isEmailValid(emailET.getText().toString())) {
                    emailET.setError("Insert a valid email.");
                    return;
                }

                if (HelpingFunctions.emailExists(emailET.getText().toString())) {
                    String validationCode = HelpingFunctions.generateRandomString(10);
                    String subject = "Password Reset Code - NO-REPLY";
                    String text = "Dear Pocket Teacher user,\n\n\tA request to change the password has been made, if you do not recognize this action, ignore this message.\nIf you intend to change it, then use the following code in the application page that opened after this request: \n\n\t\t\t" + validationCode + "\n\nThe code becomes invalid once you exit the page.\n\n\n\tBest regards,\n\t\t\tPocket Teacher team";

                    HelpingFunctions.sendEmail(emailET.getText().toString(), subject, text);

                    Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                    intent.putExtra("resetCode", validationCode);
                    intent.putExtra("email", emailET.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    finish();
                } else {
                    emailET.setError("No available account for this email.");
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        // Hide keyboard
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
        ForgotPassword.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
