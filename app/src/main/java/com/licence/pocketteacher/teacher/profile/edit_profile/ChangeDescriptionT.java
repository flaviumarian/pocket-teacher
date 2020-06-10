package com.licence.pocketteacher.teacher.profile.edit_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;

public class ChangeDescriptionT extends AppCompatActivity {

    private ImageView backIV;
    private EditText descriptionET;
    private TextView charactersTV;
    private Button resetBttn;

    private String currentDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_description_t);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Text
        descriptionET = findViewById(R.id.descriptionET);

        // Text View
        charactersTV = findViewById(R.id.charactersTV);

        // Button
        resetBttn = findViewById(R.id.resetBttn);

        // String
        currentDescription = MainPageT.teacher.getProfileDescription();
        if(currentDescription.equals("null")){
            String charactersLeft = "200 characters left";
            charactersTV.setText(charactersLeft);
            descriptionET.setText("");
        }else{
            String charactersLeft = (200 - currentDescription.length()) + " characters left";
            charactersTV.setText(charactersLeft);
            descriptionET.setText(currentDescription);
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

        // Edit Text
        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textToShow = (200 - s.length()) + " characters left";
                charactersTV.setText(textToShow);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // only 8 rows allowed
                if (descriptionET.getLayout().getLineCount() > 8) {
                    descriptionET.getText().delete(descriptionET.getText().length() - 1, descriptionET.getText().length());
                }
            }
        });

        // Button
        resetBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentDescription.equals("null")){
                    String charactersLeft = "200 characters left";
                    charactersTV.setText(charactersLeft);
                    descriptionET.setText("");
                }else{
                    String charactersLeft = (200 - currentDescription.length()) + "";
                    charactersTV.setText(charactersLeft);
                    descriptionET.setText(currentDescription);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        String newDescription = descriptionET.getText().toString();

        if(HelpingFunctions.isEditTextEmpty(descriptionET)){
            if(!currentDescription.equals("null")){
                MainPageT.teacher.setProfileDescription("null");
                HelpingFunctions.setProfileDescriptionBasedOnUsername(MainPageT.teacher.getUsername(), "null");
            }
        } else if(!currentDescription.equals(newDescription)){
            MainPageT.teacher.setProfileDescription(newDescription);
            HelpingFunctions.setProfileDescriptionBasedOnUsername(MainPageT.teacher.getUsername(), newDescription);
        }


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
