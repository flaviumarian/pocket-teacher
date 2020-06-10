package com.licence.pocketteacher.teacher.profile.edit_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;

public class ChangeGenderT extends AppCompatActivity {

    private ImageView backIV;
    private CheckBox maleCB, femaleCB, notSpecifiedCB;
    private Button resetBttn;

    private String currentGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_gender_t);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // Image View
        backIV = findViewById(R.id.backIV);

        // Button
        resetBttn = findViewById(R.id.resetBttn);

        // CheckBoxes
        maleCB = findViewById(R.id.maleCB);
        femaleCB = findViewById(R.id.femaleCB);
        notSpecifiedCB = findViewById(R.id.notSpecifiedCB);

        // String
        currentGender = MainPageT.teacher.getGender();
        switch(currentGender){
            case "0":
                maleCB.setChecked(true);
                break;
            case "1":
                femaleCB.setChecked(true);
                break;
            case "2":
                notSpecifiedCB.setChecked(true);
                break;
        }
    }

    private void setListeners(){

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Button
        resetBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentGender) {
                    case "0":
                        maleCB.setChecked(true);
                        femaleCB.setChecked(false);
                        notSpecifiedCB.setChecked(false);
                        break;
                    case "1":
                        maleCB.setChecked(false);
                        femaleCB.setChecked(true);
                        notSpecifiedCB.setChecked(false);
                        break;
                    case "2":
                        maleCB.setChecked(false);
                        femaleCB.setChecked(false);
                        notSpecifiedCB.setChecked(true);
                        break;
                }
            }
        });

        // Check Boxes
        maleCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    femaleCB.setChecked(false);
                    notSpecifiedCB.setChecked(false);
                }else if(!femaleCB.isChecked() && !notSpecifiedCB.isChecked()){
                    maleCB.setChecked(true);
                }
            }
        });

        femaleCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    maleCB.setChecked(false);
                    notSpecifiedCB.setChecked(false);
                }else if(!maleCB.isChecked() && !notSpecifiedCB.isChecked()){
                    femaleCB.setChecked(true);
                }
            }
        });

        notSpecifiedCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    maleCB.setChecked(false);
                    femaleCB.setChecked(false);
                }else if(!maleCB.isChecked() && !femaleCB.isChecked()){
                    notSpecifiedCB.setChecked(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        String newGender = null;

        if(maleCB.isChecked()){
            newGender = "0";
        }
        if(femaleCB.isChecked()) {
            newGender = "1";
        }
        if(notSpecifiedCB.isChecked()){
            newGender = "2";
        }

        if(!currentGender.equals(newGender)){
            MainPageT.teacher.setGender(newGender);
            HelpingFunctions.setGenderBasedOnUsername(MainPageT.teacher.getUsername(), newGender);
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
