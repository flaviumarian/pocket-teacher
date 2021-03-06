package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.miscellaneous.OnboardingSliderAdapterS;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.aiding_classes.Student;

public class OnboardingS extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotLinearLayout;
    private Button backBttn, nextBttn, skipBttn;

    private OnboardingSliderAdapterS onboardingSliderAdapterS;
    private TextView[] dotsTV;
    private int currentPage;

    Student student;
    private int flag; // 0 - normal, 1 - facebook, 2 - google


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_s);

        getLoginIntent();
        initiateComponents();
        setUpListeners();

    }

    private void getLoginIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            student = bundle.getParcelable("student");
            flag = bundle.getInt("flag");
        }
    }

    private void initiateComponents(){

        // View pager
        viewPager = findViewById(R.id.viewPager);

        // Linear Layout
        dotLinearLayout = findViewById(R.id.dotLinearLayout);

        // Onboarding Slider Adapter
        onboardingSliderAdapterS = new OnboardingSliderAdapterS(this);

        // Buttons
        backBttn = findViewById(R.id.backBttn);
        nextBttn = findViewById(R.id.nextBttn);
        skipBttn = findViewById(R.id.skipBttn);

    }

    private void setUpListeners(){

        // View Pager
        viewPager.setAdapter(onboardingSliderAdapterS);
        addDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                addDots(position);
                currentPage = position;

                if(currentPage == 0 ){
                    nextBttn.setEnabled(true);
                    nextBttn.setText(R.string.message_onboarding_1);
                    nextBttn.setTextSize(14);
                    backBttn.setEnabled(false);
                    backBttn.setVisibility(View.INVISIBLE);
                    skipBttn.setEnabled(true);
                    skipBttn.setVisibility(View.VISIBLE);
                    return;
                }

                if(currentPage > 0 && currentPage < 3){
                    nextBttn.setEnabled(true);
                    nextBttn.setText(R.string.message_onboarding_1);
                    nextBttn.setTextSize(14);
                    backBttn.setEnabled(true);
                    backBttn.setText(R.string.message_onboarding_2);
                    backBttn.setVisibility(View.VISIBLE);
                    skipBttn.setEnabled(true);
                    skipBttn.setVisibility(View.VISIBLE);
                    return;
                }

                if(currentPage == 3){
                    nextBttn.setEnabled(true);
                    nextBttn.setText(R.string.message_onboarding_4);
                    nextBttn.setTextSize(20);
                    backBttn.setEnabled(true);
                    backBttn.setText(R.string.message_onboarding_2);
                    backBttn.setVisibility(View.VISIBLE);
                    skipBttn.setEnabled(false);
                    skipBttn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        // Buttons
        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });

        nextBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 3){
                    exitOnboarding();
                } else{
                    viewPager.setCurrentItem(currentPage + 1);
                }
            }
        });

        skipBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitOnboarding();
            }
        });
    }

    private void addDots(int position){

        dotsTV = new TextView[4];
        dotLinearLayout.removeAllViews();

        for(int i=0; i<dotsTV.length; i++){
            dotsTV[i] = new TextView(this);
            dotsTV[i].setText(Html.fromHtml("&#8226;"));
            dotsTV[i].setTextSize(35);
            dotsTV[i].setTextColor(Color.WHITE);

            dotLinearLayout.addView(dotsTV[i]);
        }

        if(dotsTV.length > 0){
            dotsTV[position].setTextColor(getResources().getColor(R.color.darkGrey));
        }
    }

    private void exitOnboarding(){

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String result = HelpingFunctions.setOnboardingBasedOnUsername(student.getUsername(), "1");
        if(result.equals("Not changed.")){
            Toast.makeText(this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("needsOnboarding", false);
        sharedPreferencesEditor.apply();

        Intent intent = new Intent(this, MainPageS.class);
        intent.putExtra("student", student);
        intent.putExtra("flag", flag);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // DISABLED OnBackPressed
    }
}
