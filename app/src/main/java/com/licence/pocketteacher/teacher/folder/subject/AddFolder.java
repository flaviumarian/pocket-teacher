package com.licence.pocketteacher.teacher.folder.subject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;

public class AddFolder extends AppCompatActivity {

    private ImageView backIV;
    private EditText folderNameET;
    private Button clearBttn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_folder);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Text
        folderNameET = findViewById(R.id.folderNameET);

        // Button
        clearBttn = findViewById(R.id.clearBttn);

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
        clearBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderNameET.setText("");
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isEditTextEmpty(folderNameET)){

            if(SubjectPage.folders.contains(folderNameET.getText().toString())){
                Snackbar.make(getCurrentFocus(), "Folder already exists.", Snackbar.LENGTH_SHORT).show();
                return;
            }


            String result = HelpingFunctions.addFolder(MainPageT.teacher.getEmail(), SubjectPage.subjectName, folderNameET.getText().toString());
            if(result.equals("Error occurred.")){
                Snackbar.make(getCurrentFocus(), "An error occurred. Please try again later", Snackbar.LENGTH_SHORT).show();
                folderNameET.setText("");
                return;
            }

            SubjectPage.folders.add(folderNameET.getText().toString());
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,  returnIntent);
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}