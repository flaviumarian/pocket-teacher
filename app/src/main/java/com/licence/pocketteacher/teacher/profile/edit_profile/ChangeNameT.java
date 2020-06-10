package com.licence.pocketteacher.teacher.profile.edit_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;

public class ChangeNameT extends AppCompatActivity {

    private ImageView backIV;
    private EditText firstNameET, lastNameET;
    private Button resetBttn;

    private String currentFirstName, currentLastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name_t);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Texts
        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);


        // Button
        resetBttn = findViewById(R.id.resetBttn);

        // Strings
        currentFirstName = MainPageT.teacher.getFirstName();
        currentLastName = MainPageT.teacher.getLastName();


        if(!currentFirstName.equals("null")){
            firstNameET.setText(currentFirstName);
        }
        if(!currentLastName.equals("null")){
            lastNameET.setText(currentLastName);
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
                firstNameET.setText(currentFirstName);
                lastNameET.setText(currentLastName);
                firstNameET.setSelection(firstNameET.length());
                lastNameET.setSelection(lastNameET.length());
            }
        });
    }

    @Override
    public void onBackPressed() {

        // FIRST name
        if(HelpingFunctions.isEditTextEmpty(firstNameET)){
            if(!currentFirstName.equals("null")){
                MainPageT.teacher.setFirstName("null");
                HelpingFunctions.setFirstNameBasedOnUsername(MainPageT.teacher.getUsername(), "null");
            }
        } else if(!currentFirstName.equals(firstNameET.getText().toString())){

            MainPageT.teacher.setFirstName(firstNameET.getText().toString());
            HelpingFunctions.setFirstNameBasedOnUsername(MainPageT.teacher.getUsername(), firstNameET.getText().toString());
        }

        // LAST NAME
        if(HelpingFunctions.isEditTextEmpty(lastNameET)){
            if(!currentLastName.equals("null")){

                MainPageT.teacher.setLastName("null");
                HelpingFunctions.setLastNameBasedOnUsername(MainPageT.teacher.getUsername(), "null");

            }
        } else if (!currentLastName.equals(lastNameET.getText().toString())) {

            MainPageT.teacher.setLastName(lastNameET.getText().toString());
            HelpingFunctions.setLastNameBasedOnUsername(MainPageT.teacher.getUsername(), lastNameET.getText().toString());
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
