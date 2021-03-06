package com.licence.pocketteacher.teacher.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;

public class ChangeUsernameT extends AppCompatActivity {

    private ImageView backIV;
    private EditText usernameET;
    private Button resetBttn;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username_t);

        initiateComponents();
        setListeners();


    }

    private void initiateComponents(){

        username = MainPageT.teacher.getUsername();

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Text
        usernameET = findViewById(R.id.usernameET);
        usernameET.setText(username);

        // Buttons
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

        // Button
        resetBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameET.setText(username);
                usernameET.setError(null);
                usernameET.setSelection(usernameET.length());
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(HelpingFunctions.isEditTextEmpty(usernameET)){
            usernameET.setError("Insert username.");
            return;
        }

        String newUsername = usernameET.getText().toString().toLowerCase();

        if(!newUsername.equals(username)) {
            if(HelpingFunctions.getUsernameBasedOnUsername(newUsername).equals("User not found")) {
                HelpingFunctions.setUsernameBasedOnUsername(username, newUsername);
                MainPageT.teacher.setUsername(newUsername);

            } else{
                usernameET.setError("Username already used.");
                return;
            }
        }

        finish();
        ChangeUsernameT.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);

    }
}
