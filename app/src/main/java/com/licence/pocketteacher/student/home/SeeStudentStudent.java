package com.licence.pocketteacher.student.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

public class SeeStudentStudent extends AppCompatActivity {

    private ImageView backIV, reportIV;
    private Dialog reportPopup, reportSentPopup;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_student_student);

        getUsernameFromIntent();
        initiateComponents();
        setListeners();

    }



    private void getUsernameFromIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }
    }

    private void initiateComponents(){

        // Image View
        backIV = findViewById(R.id.backIV);

        reportIV = findViewById(R.id.reportIV);

        // Toolbar username
        TextView toolbarUsernameTV = findViewById(R.id.toolbarUsernameTV);
        toolbarUsernameTV.setText(username);

        // Profile picture
        ImageView profilePictureIV = findViewById(R.id.profilePictureIV);
        String image = HelpingFunctions.getProfileImageBasedOnUsername(username);
        if(image.equals("")){
            switch (HelpingFunctions.getGenderBasedOnUsername(username)) {
                case "0":
                    profilePictureIV.setImageResource(R.drawable.profile_picture_male);
                    break;
                case "1":
                    profilePictureIV.setImageResource(R.drawable.profile_picture_female);
                    break;
                case "2":
                    profilePictureIV.setImageResource(0);
                    break;
            }
        }else{
            profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(image));
        }

        // Name
        TextView nameTV = findViewById(R.id.nameTV);
        String firstName = HelpingFunctions.getFirstNameBasedOnUsername(username);
        String lastName = HelpingFunctions.getLastNameBasedOnUsername(username);
        if(firstName.equals("") || lastName.equals("")){
            nameTV.setText(R.string.message_unknown_name);
        }else{
            String name = firstName + " " + lastName;
            nameTV.setText(name);
        }

        // University
        TextView universityTV = findViewById(R.id.universityTV);
        String university = HelpingFunctions.getUniversityBasedOnUsername(username);
        if(university.equals("")){
            universityTV.setText("");
        }else {
            universityTV.setText(university);
        }

        // Followers number
        TextView followingTV = findViewById(R.id.followingTV);
        followingTV.setText(HelpingFunctions.getFollowing(username));

        // Description
        TextView descriptionTV = findViewById(R.id.descriptionTV);
        String description = HelpingFunctions.getProfileDescriptionBasedOnUsername(username);
        if(description.equals("")){
            descriptionTV.setText(R.string.message_no_description);
        }else {
            descriptionTV.setText(description);
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

        reportIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportPopup = new Dialog(SeeStudentStudent.this);
                reportPopup.setContentView(R.layout.popup_report);

                ImageView closePopupIV = reportPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportPopup.dismiss();
                    }
                });

                reportPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                reportPopup.show();

                final EditText titleET = reportPopup.findViewById(R.id.titleET);
                final EditText messageET = reportPopup.findViewById(R.id.messageET);
                Button sendBttn = reportPopup.findViewById(R.id.sendBttn);

                sendBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean canSend = true;

                        if (HelpingFunctions.isEditTextEmpty(titleET)) {
                            titleET.setError("Insert title");
                            canSend = false;
                        }
                        if (HelpingFunctions.isEditTextEmpty(messageET)) {
                            messageET.setError("Insert message");
                            canSend = false;
                        }

                        if (canSend) {

                            String result = HelpingFunctions.sendReportStudent(MainPageS.student.getEmail(), titleET.getText().toString(), messageET.getText().toString());
                            reportPopup.dismiss();
                            if (result.equals("Report sent.")) {
                                reportSentPopup = new Dialog(SeeStudentStudent.this);
                                reportSentPopup.setContentView(R.layout.popup_report_sent);

                                ImageView closePopupIV = reportSentPopup.findViewById(R.id.closePopupIV);
                                closePopupIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reportSentPopup.dismiss();

                                    }
                                });

                                reportSentPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                reportSentPopup.show();
                            }

                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
