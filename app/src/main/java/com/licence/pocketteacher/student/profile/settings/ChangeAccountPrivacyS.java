package com.licence.pocketteacher.student.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

public class ChangeAccountPrivacyS extends AppCompatActivity {

    private ImageView backIV;
    private TextView privacyTV, info1TV;
    private Switch privacySW;
    private Button resetBttn;


    private String initialPrivacy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account_privacy_s);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // initial privacy
        initialPrivacy = MainPageS.student.getPrivacy();

        // Image View
        backIV = findViewById(R.id.backIV);

        // Text Views
        privacyTV = findViewById(R.id.privacyTV);
        info1TV = findViewById(R.id.info1TV);

        // Switch
        privacySW = findViewById(R.id.privacySW);
        if(initialPrivacy.equals("0")){
            // public
            privacyTV.setText(R.string.message_change_privacy_4);
            info1TV.setText(R.string.message_change_privacy_6);
            privacySW.setChecked(false);
        }else{
            // private
            privacyTV.setText(R.string.message_change_privacy_5);
            info1TV.setText(R.string.message_change_privacy_7);
            privacySW.setChecked(true);
        }

        // Button
        resetBttn = findViewById(R.id.resetBttn);

    }

    private void setListeners(){

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Switch
        privacySW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!privacySW.isChecked()){
                    // public
                    privacyTV.setText(R.string.message_change_privacy_4);
                    info1TV.setText(R.string.message_change_privacy_6);
                    privacySW.setChecked(false);
                } else{
                    // private
                    privacyTV.setText(R.string.message_change_privacy_5);
                    info1TV.setText(R.string.message_change_privacy_7);
                    privacySW.setChecked(true);

                }
            }
        });

        // Button
        resetBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(initialPrivacy.equals("0")){
                    // public
                    privacyTV.setText(R.string.message_change_privacy_4);
                    info1TV.setText(R.string.message_change_privacy_6);
                    privacySW.setChecked(false);
                }else{
                    // private
                    privacyTV.setText(R.string.message_change_privacy_5);
                    info1TV.setText(R.string.message_change_privacy_7);
                    privacySW.setChecked(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        String newPrivacy;

        if(privacySW.isChecked()){
            newPrivacy = "1";
        } else{
            newPrivacy = "0";
        }

        if(!initialPrivacy.equals(newPrivacy)){
            MainPageS.student.setPrivacy(newPrivacy);
            HelpingFunctions.setPrivacyBasedOnUsername(MainPageS.student.getUsername(), newPrivacy);
        }

        finish();
        ChangeAccountPrivacyS.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);

    }
}
