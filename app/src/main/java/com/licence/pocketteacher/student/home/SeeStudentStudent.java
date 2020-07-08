package com.licence.pocketteacher.student.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.messaging.MessagingPage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;

public class SeeStudentStudent extends AppCompatActivity {

    private ImageView backIV, reportIV;
    private Dialog reportPopup, reportSentPopup;
    private CardView messagesC;

    private String username, imageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_student_student);

        getUsernameFromIntent();
        initiateComponents();

    }

    private void getUsernameFromIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }
    }

    private void initiateComponents(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // CardView
                messagesC = findViewById(R.id.messagesC);

                // Image View
                backIV = findViewById(R.id.backIV);
                reportIV = findViewById(R.id.reportIV);

                // Toolbar username
                final TextView toolbarUsernameTV = findViewById(R.id.toolbarUsernameTV);

                // Profile picture
                final ImageView profilePictureIV = findViewById(R.id.profilePictureIV);
                imageBase64 = HelpingFunctions.getProfileImageBasedOnUsername(username);

                // Name
                final TextView nameTV = findViewById(R.id.nameTV);
                final String firstName = HelpingFunctions.getFirstNameBasedOnUsername(username);
                final String lastName = HelpingFunctions.getLastNameBasedOnUsername(username);


                // University
                final TextView universityTV = findViewById(R.id.universityTV);
                final String university = HelpingFunctions.getUniversityBasedOnUsername(username);


                // Followers number
                final TextView followingTV = findViewById(R.id.followingTV);

                // Description
                final TextView descriptionTV = findViewById(R.id.descriptionTV);
                final String description = HelpingFunctions.getProfileDescriptionBasedOnUsername(username);

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Toolbar username
                            toolbarUsernameTV.setText(username);

                            // Profile picture
                            if(imageBase64.equals("")){
                                switch (HelpingFunctions.getGenderBasedOnUsername(username)) {
                                    case "0":
                                        profilePictureIV.setImageResource(R.drawable.profile_picture_male);
                                        break;
                                    case "1":
                                        profilePictureIV.setImageResource(R.drawable.profile_picture_female);
                                        break;
                                    case "2":
                                        profilePictureIV.setImageResource(R.drawable.profile_picture_neutral);
                                        break;
                                }
                            }else{
                                profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(imageBase64));
                            }

                            // Name
                            if(firstName.equals("") || lastName.equals("")){
                                nameTV.setText(R.string.message_unknown_name);
                            }else{
                                String name = firstName + " " + lastName;
                                nameTV.setText(name);
                            }


                            // University
                            if(university.equals("")){
                                universityTV.setText("");
                            }else {
                                universityTV.setText(university);
                            }

                            // Followers number
                            followingTV.setText(HelpingFunctions.getFollowingNumber(username));

                            // Description
                            if(description.equals("")){
                                descriptionTV.setText(R.string.message_no_description);
                            }else {
                                descriptionTV.setText(description);
                            }

                            setListeners();


                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        }).start();

    }

    private void setListeners(){

        // Card View
        messagesC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), MessagingPage.class);
                intent.putExtra("username_sender", MainPageS.student.getUsername());
                intent.putExtra("type", 0);
                intent.putExtra("username_receiver", username);
                intent.putExtra("blocked", 0);
                intent.putExtra("image", imageBase64);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });


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

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

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
