package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.miscellaneous.PasswordEncryptDecrypt;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.PrivacyPolicy;
import com.licence.pocketteacher.miscellaneous.TermsAndConditions;


public class Registration extends AppCompatActivity {

    private TextView passwordProgressTV, confirmPasswordProgressTV, termsAndConditionsTV, privacyPolicyTV;
    private EditText usernameET, emailET, passwordET, confirmPasswordET, verificationET;
    private Button signUpBttn;
    private ProgressBar passwordPB, confirmPasswordPB;
    private ImageView backIV, studentIV, teacherIV;
    private LinearLayout bottomLayout;
    private View view;
    private Dialog verificationRequestPopup, verificationRequestSent, goBackPopup;

    private final int HIDE_PASSWORD = 0;
    private final int SHOW_PASSWORD = 1;
    private final int NOT_SELECTED = 0;
    private final int SELECTED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Allow the main thread to perform a task that involves communicating with the db
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initiateComponents();

    }


    private void initiateComponents(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Current View
                view = findViewById(android.R.id.content);

                // Text Views
                passwordProgressTV = findViewById(R.id.passwordProgressTV);
                confirmPasswordProgressTV = findViewById(R.id.confirmPasswordProgressTV);
                termsAndConditionsTV = findViewById(R.id.termsAndConditionsTV);
                privacyPolicyTV = findViewById(R.id.privacyPolicyTV);

                // Edit Texts
                usernameET = findViewById(R.id.usernameET);
                emailET = findViewById(R.id.emailET);
                passwordET = findViewById(R.id.passwordET);
                confirmPasswordET = findViewById(R.id.confirmPasswordET);
                verificationET = findViewById(R.id.verificationET);

                // Buttons
                signUpBttn = findViewById(R.id.signUpBttn);

                // Progress Bars
                passwordPB = findViewById(R.id.passwordPB);
                confirmPasswordPB = findViewById(R.id.confirmPasswordPB);

                // Image Views
                backIV = findViewById(R.id.backIV);
                studentIV = findViewById(R.id.studentIV);
                studentIV.setTag(NOT_SELECTED);
                teacherIV = findViewById(R.id.teacherIV);
                teacherIV.setTag(NOT_SELECTED);

                // LinerLayouts
                bottomLayout = findViewById(R.id.bottomLayout);

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            setOnClickListeners();

                            setUpPasswordListeners();


                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickListeners(){

        // Image Views
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        studentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(studentIV.getTag().equals(0)){
                    studentIV.setImageResource(R.drawable.logo_tick);
                    studentIV.setTag(SELECTED);

                    setVisibilityOfCommonComponents(1);

                    if(teacherIV.getTag().equals(1)){
                        teacherIV.setImageResource(R.drawable.logo_teacher);
                        teacherIV.setTag(NOT_SELECTED);
                        verificationET.setVisibility(View.INVISIBLE);
                    }
                }else{

                    studentIV.setImageResource(R.drawable.logo_student);
                    studentIV.setTag(NOT_SELECTED);

                    setVisibilityOfCommonComponents(0);
                }

            }
        });
        teacherIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(teacherIV.getTag().equals(0)){
                    teacherIV.setImageResource(R.drawable.logo_tick);
                    teacherIV.setTag(SELECTED);

                    setVisibilityOfCommonComponents(1);
                    verificationET.setVisibility(View.VISIBLE);

                    if(studentIV.getTag().equals(1)){
                        studentIV.setImageResource(R.drawable.logo_teacher);
                        studentIV.setTag(NOT_SELECTED);
                    }
                } else {

                    teacherIV.setImageResource(R.drawable.logo_teacher);
                    teacherIV.setTag(NOT_SELECTED);


                    setVisibilityOfCommonComponents(0);
                    verificationET.setVisibility(View.INVISIBLE);
                }
            }
        });


        // Button
        signUpBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


        // Verification code
        verificationET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= (verificationET.getRight() - verificationET.getCompoundDrawables()[2].getBounds().width())) {
                    Snackbar.make(view, "Don't have one? Request it here: ", Snackbar.LENGTH_LONG).setAction("REQUEST", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(!HelpingFunctions.isConnected(getApplicationContext())){
                                Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            verificationRequestPopup = new Dialog(Registration.this);
                            verificationRequestPopup.setContentView(R.layout.popup_request_verification_code);

                            ImageView closePopupIV = verificationRequestPopup.findViewById(R.id.closePopupIV);
                            closePopupIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    verificationRequestPopup.dismiss();
                                }
                            });

                            verificationRequestPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            verificationRequestPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            verificationRequestPopup.show();

                            final EditText emailET = verificationRequestPopup.findViewById(R.id.emailET);
                            final EditText detailsET = verificationRequestPopup.findViewById(R.id.detailsET);

                            Button sendBttn = verificationRequestPopup.findViewById(R.id.sendBttn);
                            sendBttn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(!HelpingFunctions.isConnected(getApplicationContext())){
                                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if(HelpingFunctions.isEditTextEmpty(emailET)){
                                        emailET.setError("Insert email.");
                                        return;
                                    }
                                    if(!HelpingFunctions.isEmailValid(emailET.getText().toString())){
                                        emailET.setError("Insert a valid email address.");
                                        return;
                                    }
                                    if(HelpingFunctions.emailExists(emailET.getText().toString())){
                                        emailET.setError("Email already used.");
                                        return;
                                    }

                                    String details;
                                    if(HelpingFunctions.isEditTextEmpty(detailsET)){
                                        details = "null";
                                    }else{
                                        details = detailsET.getText().toString();
                                    }
                                    String result = HelpingFunctions.requestVerificationCode(emailET.getText().toString(), details);
                                    verificationRequestPopup.dismiss();
                                    if(result.equals("Request sent.")){
                                        verificationRequestSent = new Dialog(Registration.this);
                                        verificationRequestSent.setContentView(R.layout.popup_request_verification_code_sent);

                                        ImageView closePopupIV = verificationRequestSent.findViewById(R.id.closePopupIV);
                                        closePopupIV.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                verificationRequestSent.dismiss();

                                            }
                                        });

                                        verificationRequestSent.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        verificationRequestSent.show();
                                    }
                                }
                            });

                        }
                    }).setActionTextColor(Color.RED).show();
                    return true;
                }
                return false;
            }
        });

        // Text Views
        termsAndConditionsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TermsAndConditions.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        privacyPolicyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });
    }

    private void setVisibilityOfCommonComponents(int flag){
        switch(flag){
            case 0:
                usernameET.setVisibility(View.INVISIBLE);
                emailET.setVisibility(View.INVISIBLE);
                passwordET.setVisibility(View.INVISIBLE);
                confirmPasswordET.setVisibility(View.INVISIBLE);
                bottomLayout.setVisibility(View.INVISIBLE);
                break;
            case 1:
                usernameET.setVisibility(View.VISIBLE);
                emailET.setVisibility(View.VISIBLE);
                passwordET.setVisibility(View.VISIBLE);
                confirmPasswordET.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                break;
        }

        usernameET.setText("");
        emailET.setText("");
        passwordET.setText("");
        confirmPasswordET.setText("");
        verificationET.setText("");

    }

    private void signUp(){

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean canRegister = true;

        // Check if fields are empty and/or valid
        if(HelpingFunctions.isEditTextEmpty(usernameET)){
            usernameET.setError("Insert username.");
            canRegister = false;
        }
        if(HelpingFunctions.isEditTextEmpty(emailET)){
            emailET.setError("Insert email.");
            canRegister = false;
        } else if(!HelpingFunctions.isEmailValid(emailET.getText().toString())){
            emailET.setError("Insert a valid email.");
            canRegister = false;
        }
        if(HelpingFunctions.isEditTextEmpty(passwordET)){
            passwordET.setError("Insert password");
            canRegister = false;
        }
        if(HelpingFunctions.isEditTextEmpty(confirmPasswordET)){
            confirmPasswordET.setError("Insert confirm password");
            canRegister = false;
        }
        if(teacherIV.getTag().equals(1)){
            if(HelpingFunctions.isEditTextEmpty(verificationET)){
                verificationET.setError("Insert code.");
                canRegister = false;
            } else if(!isVerificationCodeGood(verificationET.getText().toString())) {
                canRegister = false;
            }
        }

        // Check if the username/email exists
        if(canRegister) {
            String result = HelpingFunctions.verifyIfUsernameOrEmailExists(usernameET.getText().toString().toLowerCase(), emailET.getText().toString().toLowerCase());
            if (result.equals("User found.")) {
                usernameET.setError("Username already used.");
                canRegister = false;
            }
            if (result.equals("Email found: students") || result.equals("Email found: teachers")) {
                emailET.setError("Email already used");
                canRegister = false;
            }
            if (result.equals("User and email found.")) {
                usernameET.setError("Username already used");
                emailET.setError("Email already used");
                canRegister = false;
            }
        }

        // Check if the password fields are in order
        if(canRegister && passwordPB.getProgress() >=3 && confirmPasswordPB.getProgress() >= 3) {

            // Encrypting the password
            try {
                String passwordToSendToDB = PasswordEncryptDecrypt.encrypt(passwordET.getText().toString()).trim();

                String confirmationCode = HelpingFunctions.generateRandomString(10);

                String messagingId;

                while(true) {
                    messagingId = HelpingFunctions.generateRandomMessageId();

                    if(HelpingFunctions.verifyIfMessagingIdExists(messagingId).equals("Not found.")){
                        break;
                    }
                }

                // Registering the user
                if(teacherIV.getTag().equals(1)){
                    // TEACHER ACCOUNT

                    String result = HelpingFunctions.registerUser(usernameET.getText().toString().toLowerCase(), emailET.getText().toString().toLowerCase(), passwordToSendToDB, verificationET.getText().toString(), confirmationCode, messagingId);
                    if(result.equals("Error occurred.")){
                        Toast.makeText(this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else{
                    // STUDENT ACCOUNT

                    String result = HelpingFunctions.registerUser(usernameET.getText().toString().toLowerCase(), emailET.getText().toString().toLowerCase(), passwordToSendToDB, "no_code", confirmationCode, messagingId);
                    if(result.equals("Error occurred.")){
                        Toast.makeText(this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Sending the verification mail
                String subject = "Welcome to Pocket Teacher - NO-REPLY";
                String text = "Dear Pocket Teacher user,\n\n\tThank you for creating your account. You will need your username/email & password to access your account, change your profile preferences and actually reap all the benefits this application provides.\nIn order to validate you account, you must use the following code when you first sign in: \n\n\t\t\t" + confirmationCode + "\n\n\n\tBest regards,\n\t\t\tPocket Teacher team";

                HelpingFunctions.sendEmail(emailET.getText().toString().toLowerCase(), subject, text);
                Intent intent = new Intent(this, ConfirmationSent.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                finish();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    /*                                    *** P A S S W O R D S ****                              */
    private void setUpPasswordListeners() {

        // PASSWORD
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordProgressTV.getVisibility() == View.INVISIBLE) {
                    passwordProgressTV.setVisibility(View.VISIBLE);
                    passwordPB.setVisibility(View.VISIBLE);
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_black_24dp, 0);
                    passwordET.setTag(HIDE_PASSWORD);
                    addListenerToEye(passwordET);
                }

                String currentPassword = passwordET.getText().toString();

                if(currentPassword.equals(confirmPasswordET.getText().toString())){
                    int confPassStrength = HelpingFunctions.passwordConditionsFulfilled(confirmPasswordET.getText().toString());
                    if(confPassStrength == 3){
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                        passwordProgressTV.setText(R.string.message_password_3);
                        passwordPB.setProgress(3);
                        confirmPasswordProgressTV.setText(R.string.message_password_7);
                        confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        confirmPasswordPB.setProgress(4);
                        return;
                    }
                    if(confPassStrength == 4){
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        passwordProgressTV.setText(R.string.message_password_6);
                        passwordPB.setProgress(4);
                        confirmPasswordProgressTV.setText(R.string.message_password_7);
                        confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        confirmPasswordPB.setProgress(4);
                        return;
                    }
                }else {
                    confirmPasswordProgressTV.setText(R.string.message_password_8);
                    confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    confirmPasswordPB.setProgress(1);
                }

                int passStrength;

                if (currentPassword.length() > 10) {
                    passStrength = HelpingFunctions.passwordConditionsFulfilled(currentPassword);
                } else {
                    passStrength = 1;
                }

                passwordPB.setProgress(passStrength);

                switch (passStrength) {
                    case 1:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        passwordProgressTV.setText(R.string.message_password_4);
                        break;
                    case 2:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.rgb(255, 140, 0)));
                        passwordProgressTV.setText(R.string.message_password_5);
                        break;
                    case 3:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                        passwordProgressTV.setText(R.string.message_password_3);
                        break;
                    case 4:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        passwordProgressTV.setText(R.string.message_password_6);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordET.getText().length() == 0) {
                    passwordProgressTV.setVisibility(View.INVISIBLE);
                    passwordPB.setVisibility(View.INVISIBLE);
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
                }
            }
        });

        // CONFIRM PASSWORD
        confirmPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (confirmPasswordProgressTV.getVisibility() == View.INVISIBLE) {
                    confirmPasswordProgressTV.setVisibility(View.VISIBLE);
                    confirmPasswordPB.setVisibility(View.VISIBLE);
                    confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_black_24dp, 0);
                    confirmPasswordET.setTag(HIDE_PASSWORD);
                    addListenerToEye(confirmPasswordET);
                }

                if (confirmPasswordET.getText().toString().equals(passwordET.getText().toString()) && passwordPB.getProgress() >= 3) {
                    confirmPasswordProgressTV.setText(R.string.message_password_7);
                    confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    confirmPasswordPB.setProgress(4);
                } else {
                    confirmPasswordProgressTV.setText(R.string.message_password_8);
                    confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    confirmPasswordPB.setProgress(1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (confirmPasswordET.getText().length() == 0) {
                    confirmPasswordProgressTV.setVisibility(View.INVISIBLE);
                    confirmPasswordPB.setVisibility(View.INVISIBLE);
                    confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
                }
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListenerToEye(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                        if (editText.getTag().equals(0)) {
                            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_off_black_24dp, 0);
                            editText.setTag(SHOW_PASSWORD);
                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_black_24dp, 0);
                            editText.setTag(HIDE_PASSWORD);
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }

                        // Move the cursor to the last character and copying the font for consistency
                        editText.setSelection(editText.length());
                        editText.setTypeface(usernameET.getTypeface());

                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private boolean isVerificationCodeGood(String verificationCode) {
        String result = HelpingFunctions.verifyTeacherVerificationCode(verificationCode);

        if(result.equals("Code not used.")) {
            // TO BE IMPLEMENTED WHEN CODE IS ASSIGNED, BUT NOT USED
            return true;
        }

        if(result.equals("Code sent.")) {
            return true;
        }

        if(result.equals("Code already used.")) {
            verificationET.setError("Code already used.");
            return false;
        }

        if(result.equals("Invalid code.")) {
            verificationET.setError("Code does not exist.");
            return false;
        }

        if(result.equals("Error occurred.")){
            Toast.makeText(this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }


        if(!HelpingFunctions.isEditTextEmpty(usernameET) || !HelpingFunctions.isEditTextEmpty(emailET) || !HelpingFunctions.isEditTextEmpty(passwordET) || !HelpingFunctions.isEditTextEmpty(confirmPasswordET) || !HelpingFunctions.isEditTextEmpty(verificationET)){
            goBackPopup = new Dialog(Registration.this);
            goBackPopup.setContentView(R.layout.popup_go_back);


            ImageView closePopupIV = goBackPopup.findViewById(R.id.closePopupIV);
            closePopupIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackPopup.dismiss();
                }
            });

            // Remove dialog background
            goBackPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            goBackPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            goBackPopup.show();


            Button yesButton = goBackPopup.findViewById(R.id.yesBttn);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!HelpingFunctions.isConnected(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    goBackPopup.dismiss();

                    // Hide keyboard
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    finish();
                    overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
                }
            });



        }else {

            // Hide keyboard
            try {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
            overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        }
    }
}
