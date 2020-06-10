package com.licence.pocketteacher.student.profile.following;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


import com.licence.pocketteacher.R;

public class SeeFollowing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_following);

    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof FragmentFollowingLandingPage) {
            finish();
            SeeFollowing.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        }else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragmentContainer, new FragmentFollowingLandingPage());
            transaction.addToBackStack("landing_page");
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentContainer, new FragmentFollowingLandingPage());
        transaction.addToBackStack("landing_page");
        transaction.commit();
    }
}
