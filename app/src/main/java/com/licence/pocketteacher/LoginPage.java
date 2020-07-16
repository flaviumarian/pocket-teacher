package com.licence.pocketteacher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.licence.pocketteacher.initial_activities.EmailConf1;
import com.licence.pocketteacher.initial_activities.ForgotPassword;
import com.licence.pocketteacher.initial_activities.OnboardingS;
import com.licence.pocketteacher.initial_activities.OnboardingT;
import com.licence.pocketteacher.initial_activities.Registration;
import com.licence.pocketteacher.initial_activities.RegistrationFbGoogle;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.miscellaneous.PasswordEncryptDecrypt;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.aiding_classes.Teacher;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginPage extends AppCompatActivity {


    private EditText usernameET, passwordET;
    private TextView signUpTV, resetPasswordTV;
    private Button logInBttn, facebookBttn, googleBttn;
    private LoginButton facebookLoginBttn;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private static int GOOGLE_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Allow the main thread to perform a task that involves communicating with the db
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        initiateComponents();
        setOnClickListeners();

        facebookLogInSetup();
        GoogleLogInSetup();

    }

    private void initiateComponents() {

        // Text Views
        signUpTV = findViewById(R.id.signUpTV);
        resetPasswordTV = findViewById(R.id.resetPasswordTV);

        // Edit Texts
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);

        // Buttons
        logInBttn = findViewById(R.id.logInBttn);
        facebookBttn = findViewById(R.id.facebookBttn);
        googleBttn = findViewById(R.id.googleBttn);

        // Login Button
        facebookLoginBttn = findViewById(R.id.facebookLoginBttn);

    }

    private void setOnClickListeners() {

        // Text Views
        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        resetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        // Buttons
        logInBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regularLogIn();
            }
        });

        facebookBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                facebookLoginBttn.performClick();
            }
        });

        googleBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, GOOGLE_SIGN_IN);
            }
        });

    }

    private void regularLogIn(){

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(this, "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        logInBttn.setEnabled(false);
        boolean canLogIn = true;

        if (HelpingFunctions.isEditTextEmpty(usernameET)) {
            usernameET.setError("Enter username/email");
            canLogIn = false;
        }
        if (HelpingFunctions.isEditTextEmpty(passwordET)) {
            passwordET.setError("Enter password");
            canLogIn = false;
        }

        if (canLogIn) {

            // PASSWORD
            String loginID = usernameET.getText().toString().toLowerCase();
            String password = passwordET.getText().toString();
            try {
                password = PasswordEncryptDecrypt.encrypt(password).trim();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginPage.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                logInBttn.setEnabled(true);
                return;
            }




            // USER DATA
            ArrayList<String> loginData = HelpingFunctions.logIn(loginID, password);
            if (loginData == null) {
                Snackbar.make(getCurrentFocus(), "Incorrect username/email and/or password.", Snackbar.LENGTH_SHORT).show();
                logInBttn.setEnabled(true);
                return;
            }

            // DEACTIVATED ACCOUNT
            if(loginData.get(4).equals("2")){
                Snackbar.make(getCurrentFocus(), "Account not available.", Snackbar.LENGTH_SHORT).show();
                return;
            }


            // Notification token registration
            registerCurrentToken(loginData.get(0));


            Intent intent;

            // STUDENT login
            if(loginData.get(12).equals("yes")){

                Student student = new Student(loginData.get(0), loginData.get(1), loginData.get(2), loginData.get(3), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9), loginData.get(10));


                // PREFERENCE save
                SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("email", loginData.get(3));
                sharedPreferencesEditor.putBoolean("loggedIn", true);
                sharedPreferencesEditor.apply();



                // EMAIL CONFIRMATION
                if(loginData.get(4).equals("0")){

                    intent = new Intent(getApplicationContext(), EmailConf1.class);
                    intent.putExtra("student", student);
                    intent.putExtra("flag", 0);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    return;
                }

                // ONBOARDING
                if(loginData.get(5).equals("0")){

                    intent = new Intent(getApplicationContext(), OnboardingS.class);
                    intent.putExtra("student", student);
                    intent.putExtra("flag", 0);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    return;
                }

                // MAIN APPLICATION
                intent = new Intent(getApplicationContext(), MainPageS.class);
                intent.putExtra("student", student);
                intent.putExtra("flag", 0);
                startActivity(intent);
                finish();
                return;
            }

            // TEACHER login
            if(loginData.get(12).equals("no")){


                Teacher teacher = new Teacher(loginData.get(0), loginData.get(1), loginData.get(2), loginData.get(3), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9), loginData.get(10), HelpingFunctions.getAllSubjectsForTeacher(loginData.get(3)));

                SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("email", loginData.get(3));
                sharedPreferencesEditor.putBoolean("loggedIn", true);
                sharedPreferencesEditor.apply();

                // EMAIL CONFIRMATION
                if(loginData.get(4).equals("0")){

                    intent = new Intent(getApplicationContext(), EmailConf1.class);
                    intent.putExtra("teacher", teacher);
                    intent.putExtra("flag", 0);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    return;
                }

                // ONBOARDING
                if(loginData.get(5).equals("0")){

                    intent = new Intent(getApplicationContext(), OnboardingT.class);
                    intent.putExtra("teacher", teacher);
                    intent.putExtra("flag", 0);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    return;
                }

                // MAIN APPLICATION
                intent = new Intent(getApplicationContext(), MainPageT.class);
                intent.putExtra("teacher", teacher);
                intent.putExtra("flag", 0);
                startActivity(intent);
                finish();
            }
        }
    }

    private void facebookLogInSetup(){


        callbackManager = CallbackManager.Factory.create();
        facebookLoginBttn.setPermissions(Arrays.asList("email", "public_profile"));
        facebookLoginBttn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(accessToken != null){
                    facebookLoadUserProfile(accessToken);
                }
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) { }
        });
    }

    private void facebookLoadUserProfile(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String email = object.getString("email");
                    ArrayList<String> loginData = HelpingFunctions.logInUserData(email);
                    Intent intent;

                    // NO EXISTING ACCOUNT
                    if(loginData == null){

                        String firstName, lastName, imageUrl;
                        firstName = object.getString("first_name");
                        lastName = object.getString("last_name");
                        String id = object.getString("id");
                        imageUrl = "https://graph.facebook.com/" + id + "/picture?type=normal";

                        try {
                            URL imageURL = new URL(imageUrl);
                            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                            String base64Image = HelpingFunctions.convertImageToBase64(bitmap);

                            intent = new Intent(getApplicationContext(), RegistrationFbGoogle.class);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("base64Image", base64Image);
                            intent.putExtra("email", email);
                            intent.putExtra("flag", 1);

                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                            finish();

                        }catch(Exception e){
                            return;
                        }
                        return;
                    }


                    // DEACTIVATED ACCOUNT
                    if(loginData.get(3).equals("2")){
                        Snackbar.make(getCurrentFocus(), "Account not available.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Notification token registration
                    registerCurrentToken(loginData.get(0));


                    // STUDENT login
                    if(loginData.get(11).equals("yes")){

                        Student student = new Student(loginData.get(0), loginData.get(1), loginData.get(2), email, loginData.get(5), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9));

                        // ONBOARDING
                        if(loginData.get(4).equals("0")){

                            intent = new Intent(getApplicationContext(), OnboardingS.class);
                            intent.putExtra("student", student);
                            intent.putExtra("flag", 1);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                            return;
                        }

                        // MAIN APPLICATION
                        intent = new Intent(getApplicationContext(), MainPageS.class);
                        intent.putExtra("student", student);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    // TEACHER login
                    if(loginData.get(11).equals("no")) {
                        Teacher teacher = new Teacher(loginData.get(0), loginData.get(1), loginData.get(2), email, loginData.get(5), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9), HelpingFunctions.getAllSubjectsForTeacher(email));

                        // ONBOARDING
                        if (loginData.get(4).equals("0")) {

                            intent = new Intent(getApplicationContext(), OnboardingT.class);
                            intent.putExtra("teacher", teacher);
                            intent.putExtra("flag", 1);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                            return;
                        }

                        // MAIN APPLICATION
                        intent = new Intent(getApplicationContext(), MainPageT.class);
                        intent.putExtra("teacher", teacher);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void GoogleLogInSetup(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void googleLoadUserProfile(Task<GoogleSignInAccount> task){
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String email = account.getEmail();
            ArrayList<String> loginData = HelpingFunctions.logInUserData(email);
            Intent intent;

            // NO EXISTING ACCOUNT
            if(loginData == null){

                String firstName, lastName;
                firstName = account.getGivenName();
                lastName = account.getFamilyName();
                String base64Image;
                if(account.getPhotoUrl() != null){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), account.getPhotoUrl());
                        base64Image = HelpingFunctions.convertImageToBase64(bitmap);
                    }catch(Exception e){
                        e.printStackTrace();
                        base64Image = "null";
                    }
                } else{
                    base64Image = "null";
                }

                intent = new Intent(getApplicationContext(), RegistrationFbGoogle.class);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("base64Image", base64Image);
                intent.putExtra("email", email);
                intent.putExtra("flag", 2);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                finish();


                return;
            }

            // DEACTIVATED ACCOUNT
            if(loginData.get(3).equals("2")){
                Snackbar.make(getCurrentFocus(), "Account not available.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            // Notification token registration
            registerCurrentToken(loginData.get(0));

            // STUDENT login
            if(loginData.get(11).equals("yes")){

                Student student = new Student(loginData.get(0), loginData.get(1), loginData.get(2), email, loginData.get(5), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9));

                // ONBOARDING
                if(loginData.get(4).equals("0")){

                    intent = new Intent(getApplicationContext(), OnboardingS.class);
                    intent.putExtra("student", student);
                    intent.putExtra("flag", 2);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    return;
                }

                // MAIN APPLICATION
                intent = new Intent(getApplicationContext(), MainPageS.class);
                intent.putExtra("student", student);
                intent.putExtra("flag", 2);
                startActivity(intent);
                finish();
                return;
            }

            // TEACHER login
            if(loginData.get(11).equals("no")) {
                Teacher teacher = new Teacher(loginData.get(0), loginData.get(1), loginData.get(2), email, loginData.get(5), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9), HelpingFunctions.getAllSubjectsForTeacher(email));

                // ONBOARDING
                if (loginData.get(4).equals("0")) {

                    intent = new Intent(getApplicationContext(), OnboardingT.class);
                    intent.putExtra("teacher", teacher);
                    intent.putExtra("flag", 2);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    return;
                }

                // MAIN APPLICATION
                intent = new Intent(getApplicationContext(), MainPageT.class);
                intent.putExtra("teacher", teacher);
                intent.putExtra("flag", 2);
                startActivity(intent);
                finish();
            }

        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void registerCurrentToken(final String username){

        FirebaseMessaging.getInstance().subscribeToTopic("pocketTeacher");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginPage.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                HelpingFunctions.registerToken(newToken, username);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // Facebook sign in
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Google sign in
        if(requestCode == GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            googleLoadUserProfile(task);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logInBttn.setEnabled(true);
        usernameET.setText("");
        usernameET.setError(null);
        passwordET.setText("");
        passwordET.setError(null);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
