package com.licence.pocketteacher;


import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.initial_activities.EmailConf1;
import com.licence.pocketteacher.initial_activities.OnboardingS;
import com.licence.pocketteacher.initial_activities.OnboardingT;
import com.licence.pocketteacher.initial_activities.RegistrationFbGoogle;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.aiding_classes.Teacher;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;


public class OpeningPage extends AppCompatActivity {

    public static final String AUTO_LOGIN = "AutoLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Allow the main thread to perform a task that involves communicating with the db
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // clear all notifications
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

    }

    private void facebookSignIn(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String email = object.getString("email");
                    ArrayList<String> loginData = HelpingFunctions.logInUserData(email);

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


                            Intent intent = new Intent(getApplicationContext(), RegistrationFbGoogle.class);
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

                    Intent intent;

                    // NO AUTO LOGIN
                    if(loginData.get(10).equals("0")){
                        intent = new Intent(getApplicationContext(), LoginPage.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    // DEACTIVATED ACCOUNT
                    if(loginData.get(3).equals("2")){
                        Snackbar.make(getCurrentFocus(), "Account not available.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }



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
                    if(loginData.get(11).equals("no")){

                        Teacher teacher = new Teacher(loginData.get(0), loginData.get(1), loginData.get(2), email, loginData.get(5), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9), HelpingFunctions.getAllSubjectsForTeacher(email));


                        // ONBOARDING
                        if(loginData.get(4).equals("0")){

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

    private void googleSignIn(GoogleSignInAccount account){
        try{
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), account.getPhotoUrl());
                    base64Image = HelpingFunctions.convertImageToBase64(bitmap);
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

            // NO AUTO LOGIN
            if(loginData.get(10).equals("0")){
                intent = new Intent(this, LoginPage.class);
                startActivity(intent);
                finish();
                return;
            }

            // DEACTIVATED ACCOUNT
            if(loginData.get(3).equals("2")){
                Snackbar.make(getCurrentFocus(), "Account not available.", Snackbar.LENGTH_SHORT).show();
                return;
            }

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

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void regularSignIn(SharedPreferences sharedPreferences){

        String email = sharedPreferences.getString("email", "");
        ArrayList<String> loginData = HelpingFunctions.logInUserData(email);
        Intent intent;


        if(loginData == null){
            intent = new Intent(this, LoginPage.class);
            startActivity(intent);
            finish();
            return;
        }


        // NO AUTO LOGIN
        if(loginData.get(10).equals("0")){
            intent = new Intent(this, LoginPage.class);
            startActivity(intent);
            finish();
            return;
        }

        // DEACTIVATED ACCOUNT
        if(loginData.get(3).equals("2")){
            Snackbar.make(getCurrentFocus(), "Account not available.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // STUDENT login
        if(loginData.get(11).equals("yes")){


            Student student = new Student(loginData.get(0), loginData.get(1), loginData.get(2), email, loginData.get(5), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9));

            // EMAIL CONFIRMATION
            if(loginData.get(3).equals("0")){

                intent = new Intent(getApplicationContext(), EmailConf1.class);
                intent.putExtra("student", student);
                intent.putExtra("flag", 0);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                return;
            }

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
            intent.putExtra("flag", 0);
            startActivity(intent);
            finish();
            return;
        }

        // TEACHER login
        if(loginData.get(11).equals("no")) {

            Teacher teacher = new Teacher(loginData.get(0), loginData.get(1), loginData.get(2), email, loginData.get(5), loginData.get(6), loginData.get(7), loginData.get(8), loginData.get(9), HelpingFunctions.getAllSubjectsForTeacher(email));


            // EMAIL CONFIRMATION
            if(loginData.get(3).equals("0")){

                intent = new Intent(getApplicationContext(), EmailConf1.class);
                intent.putExtra("teacher", teacher);
                intent.putExtra("flag", 0);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                return;
            }

            // ONBOARDING
            if (loginData.get(4).equals("0")) {

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


    @Override
    protected void onStart() {
        super.onStart();

        // Facebook
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            facebookSignIn(accessToken);
            return;
        }

        // Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            googleSignIn(account);
            return;
        }


        // Regular login
        SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0);
        if(sharedPreferences.getBoolean("loggedIn", false)){
            regularSignIn(sharedPreferences);
            return;
        }


        // NO ACCOUNT LOGGED IN
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
        finish();
    }

}
