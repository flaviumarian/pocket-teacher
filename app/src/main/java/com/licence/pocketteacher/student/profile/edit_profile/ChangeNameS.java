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

public class ChangeNameS extends AppCompatActivity {

    private ImageView backIV;
    private EditText firstNameET, lastNameET;
    private Button resetBttn;

    private String currentFirstName, currentLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name_s);


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
        currentFirstName = MainPageS.student.getFirstName();
        currentLastName = MainPageS.student.getLastName();


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
                MainPageS.student.setFirstName("null");
                HelpingFunctions.setFirstNameBasedOnUsername(MainPageS.student.getUsername(), "null");
            }
        } else if(!currentFirstName.equals(firstNameET.getText().toString())){

            MainPageS.student.setFirstName(firstNameET.getText().toString());
            HelpingFunctions.setFirstNameBasedOnUsername(MainPageS.student.getUsername(), firstNameET.getText().toString());
        }


        // LAST NAME
        if(HelpingFunctions.isEditTextEmpty(lastNameET)){
            if(!currentLastName.equals("null")){

                MainPageS.student.setLastName("null");
                HelpingFunctions.setLastNameBasedOnUsername(MainPageS.student.getUsername(), "null");

            }
        } else if (!currentLastName.equals(lastNameET.getText().toString())) {

            MainPageS.student.setLastName(lastNameET.getText().toString());
            HelpingFunctions.setLastNameBasedOnUsername(MainPageS.student.getUsername(), lastNameET.getText().toString());
        }


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
