package com.licence.pocketteacher.messaging.new_conversation_student_account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

public class StartConversationListStudent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_conversation_list_student);
    }


    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof FragmentStudentConvLandingPage) {
            finish();
            StartConversationListStudent.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        }else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragmentContainer, new FragmentStudentConvLandingPage());
            transaction.addToBackStack("landing_page");
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentContainer, new FragmentStudentConvLandingPage());
        transaction.addToBackStack("landing_page");
        transaction.commit();
    }
}
