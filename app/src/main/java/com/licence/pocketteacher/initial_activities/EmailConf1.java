package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

public class EmailConf1 extends AppCompatActivity {

    private EditText confirmCodeET;
    private TextView bottomTV, reSendTV;
    private Button verifyBttn;
    private ImageView backIV;

    private Student student;
    private Teacher teacher;

    private boolean isStudent;
    public static boolean fromLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_conf1);


        // Allow the main thread to perform a task that involves communicating with the db
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        getLoginIntent();
        initiateComponents();
        setOnClickListeners();

    }

    private void getLoginIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            student = bundle.getParcelable("student");
            teacher = bundle.getParcelable("teacher");
        }

        if(student == null){
            isStudent = false;
        } else {
            isStudent = true;
        }
    }

    private void initiateComponents(){
        // Edit Text
        confirmCodeET = findViewById(R.id.confirmCodeET);

        // Text Views
        bottomTV = findViewById(R.id.bottomTV);
        reSendTV = findViewById(R.id.reSendTV);

        // Button
        verifyBttn = findViewById(R.id.verifyBttn);

        // Image View
        backIV = findViewById(R.id.backIV);
    }

    private void setOnClickListeners(){
        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Button
        verifyBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HelpingFunctions.isEditTextEmpty(confirmCodeET)){
                    confirmCodeET.setError("Insert code.");
                    return;
                }

                String username;
                if(isStudent){
                    username = student.getUsername();
                } else{
                    username = teacher.getUsername();
                }

                String result = HelpingFunctions.verifyEmail(username, confirmCodeET.getText().toString());
                if (result.equals("Confirmation done.")) {

                    SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
                    SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                    sharedPreferencesEditor.putBoolean("needsConfirmation", false);
                    sharedPreferencesEditor.apply();

                    Intent intent = new Intent(getApplicationContext(), EmailConf2.class);
                    intent.putExtra("student", student);
                    intent.putExtra("teacher", teacher);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    return;
                }
                if (result.equals("Error occurred.")) {
                    confirmCodeET.setError("Confirmation failed. Please try again later.");
                    return;
                }
                if (result.equals("Wrong code.")) {
                    confirmCodeET.setError("Wrong code.");
                }
            }
        });

        // Text View
        reSendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmationCode, email;

                if(isStudent){
                    confirmationCode = HelpingFunctions.getConfirmationCodeBasedOnUsername(student.getUsername());
                    email = student.getEmail();
                } else{
                    confirmationCode = HelpingFunctions.getConfirmationCodeBasedOnUsername(teacher.getUsername());
                    email = teacher.getEmail();
                }

                String text;

                if(fromLogIn){
                    text = "Dear Pocket Teacher user,\n\n\t\nIn order to validate you account, you must use the following code when you first sign in: \n\n\t\t\t" + confirmationCode + "\n\n\n\tBest regards,\n\t\t\tPocket Teacher team";
                }else{
                    text = "Dear Pocket Teacher user,\n\n\t\nIn order to validate you account, you must use the following code: \n\n\t\t\t" + confirmationCode + "\n\n\n\tBest regards,\n\t\t\tPocket Teacher team";
                }
                String subject = "Confirmation Code - NO-REPLY";

                HelpingFunctions.sendEmail(email, subject, text);

                bottomTV.setText(R.string.message_confirm_email_4);
                reSendTV.setVisibility(View.INVISIBLE);

                Snackbar.make(v, "Mail re-sent.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        // Log out the user
        SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("loggedIn", false);
        sharedPreferencesEditor.apply();

        // Go to Login page
        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
