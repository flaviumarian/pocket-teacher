package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.student.MainPageS;

public class EmailConf2 extends AppCompatActivity {

    private GifImageView lockedGifImageView;
    private Button getStartedBttn;

    private Student student;
    private Teacher teacher;

    private boolean isStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_conf2);

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
        // Gif Image View
        lockedGifImageView = findViewById(R.id.gifImageView);

        // Button
        getStartedBttn = findViewById(R.id.getStartedBttn);

    }

    private void setOnClickListeners(){
        // Gif Image View
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Gif starts after 1.5s
                ((GifDrawable) lockedGifImageView.getDrawable()).start();
            }
        }, 1500);

        handler.postDelayed(new Runnable() {
            public void run() {
                // Gif stops after 3.2s
                ((GifDrawable) lockedGifImageView.getDrawable()).stop();
            }
        }, 1600);


        // Button
        getStartedBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result;
                if(isStudent){
                    result = HelpingFunctions.getOnboardingBasedOnUsername(student.getUsername());
                } else{
                    result = HelpingFunctions.getOnboardingBasedOnUsername(teacher.getUsername());
                }

                Intent intent;

                if(result.equals("0")){
                    if(isStudent) {
                        intent = new Intent(getApplicationContext(), OnboardingS.class);
                    } else {
                        intent = new Intent(getApplicationContext(), OnboardingT.class);
                    }

                    intent.putExtra("student", student);
                    intent.putExtra("teacher", teacher);
                    intent.putExtra("flag", 0); // normal login, not facebook/google
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    finish();
                    return;
                }

                if(isStudent){
                    intent = new Intent(getApplicationContext(), MainPageS.class);
                } else{
                    intent = new Intent(getApplicationContext(), MainPageT.class);
                }

                intent.putExtra("student", student);
                intent.putExtra("teacher", teacher);
                intent.putExtra("flag", 0); // normal login, not facebook/google
                startActivity(intent);
                finish();
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