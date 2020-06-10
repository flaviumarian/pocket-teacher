package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.miscellaneous.PasswordEncryptDecrypt;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.aiding_classes.Teacher;

public class RegistrationFbGoogle extends AppCompatActivity {

    private ImageView backIV, studentIV, teacherIV;
    private EditText verificationET;
    private Button getStartedBttn;
    private View view;
    private Dialog verificationRequestPopup, verificationRequestSent;

    private final int NOT_SELECTED = 0;
    private final int SELECTED = 1;

    private String username, email, firstName, lastName, base64Image;
    private int flag; // 0 - normal, 1 - facebook, 2 - google


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_fb_google);

        getLoginIntent();
        initiateComponents();
        setOnClickListeners();

    }

    private void getLoginIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            email = bundle.getString("email");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            base64Image = bundle.getString("base64Image");
            flag = bundle.getInt("flag");
        }
    }

    private void initiateComponents(){

        // Current View
        view = findViewById(android.R.id.content);

        // Image Views
        backIV = findViewById(R.id.backIV);
        studentIV = findViewById(R.id.studentIV);
        studentIV.setTag(NOT_SELECTED);
        teacherIV = findViewById(R.id.teacherIV);
        teacherIV.setTag(NOT_SELECTED);

        // Edit Text
        verificationET = findViewById(R.id.verificationET);

        // Button
        getStartedBttn = findViewById(R.id.getStartedBttn);
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

                    if(teacherIV.getTag().equals(1)){
                        teacherIV.setImageResource(R.drawable.logo_teacher);
                        teacherIV.setTag(NOT_SELECTED);
                        verificationET.setVisibility(View.INVISIBLE);
                    }
                }else{
                    studentIV.setImageResource(R.drawable.logo_student);
                    studentIV.setTag(NOT_SELECTED);
                    verificationET.setVisibility(View.INVISIBLE);
                }

            }
        });

        teacherIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(teacherIV.getTag().equals(0)){
                    teacherIV.setImageResource(R.drawable.logo_tick);
                    teacherIV.setTag(SELECTED);

                    verificationET.setVisibility(View.VISIBLE);
                    verificationET.setText("");

                    if(studentIV.getTag().equals(1)){
                        studentIV.setImageResource(R.drawable.logo_teacher);
                        studentIV.setTag(NOT_SELECTED);
                    }
                } else {

                    teacherIV.setImageResource(R.drawable.logo_teacher);
                    teacherIV.setTag(NOT_SELECTED);

                    verificationET.setVisibility(View.INVISIBLE);
                }
            }
        });


        // Edit text
        verificationET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= (verificationET.getRight() - verificationET.getCompoundDrawables()[2].getBounds().width())){
                    Snackbar.make(view, "Don't have one? Request it here: ", Snackbar.LENGTH_LONG).setAction("REQUEST", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            verificationRequestPopup = new Dialog(RegistrationFbGoogle.this);
                            verificationRequestPopup.setContentView(R.layout.popup_request_verification_code);

                            ImageView closePopupIV = verificationRequestPopup.findViewById(R.id.closePopupIV);
                            closePopupIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    verificationRequestPopup.dismiss();
                                }
                            });

                            // Remove dialog background
                            verificationRequestPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            verificationRequestPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            verificationRequestPopup.show();

                            final EditText emailET = verificationRequestPopup.findViewById(R.id.emailET);
                            final EditText detailsET = verificationRequestPopup.findViewById(R.id.detailsET);

                            Button sendBttn = verificationRequestPopup.findViewById(R.id.sendBttn);
                            sendBttn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
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
                                        verificationRequestSent = new Dialog(RegistrationFbGoogle.this);
                                        verificationRequestSent.setContentView(R.layout.popup_request_verification_code_sent);

                                        ImageView closePopupIV = verificationRequestSent.findViewById(R.id.closePopupIV);
                                        closePopupIV.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                verificationRequestSent.dismiss();

                                            }
                                        });

                                        // Remove dialog background
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

        // Button
        getStartedBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(studentIV.getTag().equals(0) && teacherIV.getTag().equals(0)){
                    Snackbar.make(view, "You need to choose a type.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(teacherIV.getTag().equals(1)){
                    if(HelpingFunctions.isEditTextEmpty(verificationET)){
                        verificationET.setError("Insert code.");
                        return;
                    }else if(!isVerificationCodeGood(verificationET.getText().toString())) {
                        return;
                    }
                }

                // Generate unique missing information needed for the account
                while(true) {
                    username = HelpingFunctions.generateUsername(firstName, lastName);
                    if(HelpingFunctions.getUsernameBasedOnUsername(username).equals("User not found.")){
                        break;
                    }
                }

                String password = HelpingFunctions.generateRandomPassword();
                try{
                    password = PasswordEncryptDecrypt.encrypt(password);
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(studentIV.getTag().equals(1)){

                    // Student account
                    String result = HelpingFunctions.registerUserFacebookGoogle(username, firstName, lastName, email, password, "no_code");
                    if(result.equals("Error occurred.")) {
                        Toast.makeText(getApplicationContext(), "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    registerCurrentToken(username);
                    Student student = new Student(username, firstName, lastName, email, "2", "null", "No description.", base64Image, "0");
                    Intent intent;

                    String resultOnboarding = HelpingFunctions.getOnboardingBasedOnUsername(username);
                    if(resultOnboarding.equals("0")){
                        intent = new Intent(getApplicationContext(), OnboardingS.class);

                        intent.putExtra("student", student);
                        intent.putExtra("flag", flag); // normal login, not facebook/google
                        startActivity(intent);
                        finish();
                        return;
                    }

                    intent = new Intent(getApplicationContext(), MainPageS.class);
                    intent.putExtra("student", student);
                    intent.putExtra("flag", flag);
                    startActivity(intent);
                    finish();
                }

                if(teacherIV.getTag().equals(1)){

                    // Teacher account
                    String result = HelpingFunctions.registerUserFacebookGoogle(username, firstName, lastName, email, password, verificationET.getText().toString());
                    if(result.equals("Error occurred.")) {
                        Toast.makeText(getApplicationContext(), "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    registerCurrentToken(username);
                    Teacher teacher = new Teacher(username, firstName, lastName, email, "2", "null", "No description.", base64Image, "0", null);
                    Intent intent;

                    String resultOnboarding = HelpingFunctions.getOnboardingBasedOnUsername(username);
                    if(resultOnboarding.equals("0")){
                        intent = new Intent(getApplicationContext(), OnboardingT.class);

                        intent.putExtra("teacher", teacher);
                        intent.putExtra("flag", flag); // normal login, not facebook/google
                        startActivity(intent);
                        finish();
                        return;
                    }

                    intent = new Intent(getApplicationContext(), MainPageT.class);
                    intent.putExtra("teacher", teacher);
                    intent.putExtra("flag", flag);
                    startActivity(intent);
                    finish();
                }
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

        return false;
    }

    private void registerCurrentToken(final String username){

        FirebaseMessaging.getInstance().subscribeToTopic("pocketTeacher");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( RegistrationFbGoogle.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                HelpingFunctions.registerToken(newToken, username);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(flag == 1) {
            LoginManager.getInstance().logOut();
        }
        if(flag == 2){
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            googleSignInClient.signOut();
        }

        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        finish();

    }
}
