package com.licence.pocketteacher.teacher.profile.followers.blocked_students;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.licence.pocketteacher.R;


public class SeeBlocked extends AppCompatActivity {

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_blocked);

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof FragmentBlockedLandingPage) {
            finish();
            SeeBlocked.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        }else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragmentContainer, new FragmentBlockedLandingPage());
            transaction.addToBackStack("landing_page");
            transaction.commit();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentContainer, new FragmentBlockedLandingPage());
        transaction.addToBackStack("landing_page");
        transaction.commit();

    }
}
