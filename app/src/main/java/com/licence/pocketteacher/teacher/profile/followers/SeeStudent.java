package com.licence.pocketteacher.teacher.profile.followers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
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
import com.licence.pocketteacher.teacher.MainPageT;

public class SeeStudent extends AppCompatActivity {

    private ImageView backIV;
    private Dialog blockPopup, blockedPopup, reportPopup, reportSentPopup;
    private CardView messagesC;

    private String username, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_student);

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

        // Toolbar
        Toolbar subjectToolbar = findViewById(R.id.studentToolbar);
        subjectToolbar.setTitle("");
        this.setSupportActionBar(subjectToolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = findViewById(R.id.backIV);

                // Toolbar username
                final TextView toolbarUsernameTV = findViewById(R.id.toolbarUsernameTV);

                // Profile picture
                final ImageView profilePictureIV = findViewById(R.id.profilePictureIV);
                image = HelpingFunctions.getProfileImageBasedOnUsername(username);


                // Name
                final TextView nameTV = findViewById(R.id.nameTV);
                final String firstName = HelpingFunctions.getFirstNameBasedOnUsername(username);
                final String lastName = HelpingFunctions.getLastNameBasedOnUsername(username);

                // Privacy
                final String privacy = HelpingFunctions.getPrivacyBasedOnUsername(username);

                // Messages
                messagesC = findViewById(R.id.messagesC);

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Toolbar username
                            toolbarUsernameTV.setText(username);

                            // Profile picture
                            if(image.equals("")){
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
                                profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(image));
                            }

                            // Name
                            if(firstName.equals("") || lastName.equals("")){
                                nameTV.setText(R.string.message_unknown_name);
                            }else{
                                String name = firstName + " " + lastName;
                                nameTV.setText(name);
                            }

                            // Privacy
                            if(privacy.equals("1")){
                                // PRIVATE
                                TextView privateTV = findViewById(R.id.privateTV);
                                privateTV.setVisibility(View.VISIBLE);

                                TextView universityTV = findViewById(R.id.universityTV);
                                universityTV.setVisibility(View.INVISIBLE);

                                CardView followersCard = findViewById(R.id.followersCard);
                                followersCard.setVisibility(View.INVISIBLE);

                                TextView infoTV = findViewById(R.id.infoTV);
                                infoTV.setVisibility(View.INVISIBLE);

                                CardView descriptionCard = findViewById(R.id.descriptionCard);
                                descriptionCard.setVisibility(View.INVISIBLE);


                            }else if (privacy.equals("0")){
                                // PUBLIC

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
                                followingTV.setText(HelpingFunctions.getFollowingNumber(username));

                                // Description
                                TextView descriptionTV = findViewById(R.id.descriptionTV);
                                String description = HelpingFunctions.getProfileDescriptionBasedOnUsername(username);
                                if(description.equals("")){
                                    descriptionTV.setText(R.string.message_no_description);
                                }else {
                                    descriptionTV.setText(description);
                                }
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

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        messagesC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }


                Intent intent = new Intent(getApplicationContext(), MessagingPage.class);
                intent.putExtra("username_sender", MainPageT.teacher.getUsername());
                intent.putExtra("type", 0);
                intent.putExtra("username_receiver", username);
                intent.putExtra("blocked", 0);
                intent.putExtra("image", image);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });
    }


    /*                      *** T O O L B A R    M E N U ***                         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_follower, menu);

        // Setting up menu options
        MenuItem item = menu.getItem(0).getSubMenu().getItem(1);
        SpannableString s = new SpannableString("Report");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        item = menu.getItem(0).getSubMenu().getItem(0);
        s = new SpannableString("Block user");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ImageView closePopupIV;

        switch (id) {
            case R.id.blockUser:

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

                blockPopup = new Dialog(SeeStudent.this);
                blockPopup.setContentView(R.layout.popup_block_student);

                closePopupIV = blockPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockPopup.dismiss();
                    }
                });

                // Remove dialog background
                blockPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                blockPopup.show();

                Button yesBttn = blockPopup.findViewById(R.id.yesBttn);
                yesBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Block user
                        String result = HelpingFunctions.blockUser(MainPageT.teacher.getUsername(), username);
                        blockPopup.dismiss();
                        if(result.equals("Completed.")){
                            blockedPopup = new Dialog(SeeStudent.this);
                            blockedPopup.setContentView(R.layout.popup_block_student_done);

                            ImageView closePopupIV = blockedPopup.findViewById(R.id.closePopupIV);
                            closePopupIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    blockedPopup.dismiss();
                                }
                            });

                            // Remove dialog background
                            blockedPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            blockedPopup.show();
                            blockedPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                    SeeStudent.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
                                }
                            });
                        }
                    }
                });

                break;
            case R.id.report:

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

                reportPopup = new Dialog(SeeStudent.this);
                reportPopup.setContentView(R.layout.popup_report);

                closePopupIV = reportPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportPopup.dismiss();
                    }
                });

                // Remove dialog background
                reportPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                reportPopup.show();

                final EditText titleET = reportPopup.findViewById(R.id.titleET);
                final EditText messageET = reportPopup.findViewById(R.id.messageET);
                Button sendBttn = reportPopup.findViewById(R.id.sendBttn);

                sendBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

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

                            String result = HelpingFunctions.sendReportTeacher(MainPageT.teacher.getEmail(), titleET.getText().toString(), messageET.getText().toString());
                            reportPopup.dismiss();
                            if (result.equals("Report sent.")) {
                                reportSentPopup = new Dialog(SeeStudent.this);
                                reportSentPopup.setContentView(R.layout.popup_report_sent);

                                ImageView closePopupIV = reportSentPopup.findViewById(R.id.closePopupIV);
                                closePopupIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reportSentPopup.dismiss();

                                    }
                                });

                                // Remove dialog background
                                reportSentPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                reportSentPopup.show();
                            }
                        }
                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
        SeeStudent.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}