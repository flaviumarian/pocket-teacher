package com.licence.pocketteacher.student.profile.edit_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

public class ChangeUniversityS extends AppCompatActivity {


    private ImageView backIV;
    private Button resetBttn;
    private EditText universityET;

    private String currentUniversity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_university_s);

        initiateComponents();
        setListeners();

    }


    private void initiateComponents(){

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Text
        universityET = findViewById(R.id.universityTV);

        // Button
        resetBttn = findViewById(R.id.resetBttn);

        // String
        currentUniversity = MainPageS.student.getUniversity();
        if(currentUniversity.equals("null")){
            universityET.setText("");
        }else {
            universityET.setText(currentUniversity);
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
                if(currentUniversity.equals("null")){
                    universityET.setText("");
                }else {
                    universityET.setText(currentUniversity);
                    universityET.setSelection(universityET.length());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(HelpingFunctions.isEditTextEmpty(universityET) && !currentUniversity.equals("null")){

            MainPageS.student.setUniversity(universityET.getText().toString());
            HelpingFunctions.setUniversityBasedOnUsername(MainPageS.student.getUsername(), "null");

        }else if(!universityET.getText().toString().equals(currentUniversity)){

            MainPageS.student.setUniversity(universityET.getText().toString());
            HelpingFunctions.setUniversityBasedOnUsername(MainPageS.student.getUsername(), universityET.getText().toString());

        }


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
