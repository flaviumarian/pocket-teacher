package com.licence.pocketteacher.student.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.messaging.MessagingPage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.aiding_classes.Teacher;
import com.licence.pocketteacher.teacher.MainPageT;

import java.util.ArrayList;

public class SeeTeacher extends AppCompatActivity {

    private ImageView backIV, reportIV, followIV;
    private TextView followersTV;
    private ListView subjectsLV;
    private SubjectsAdapter subjectsAdapter;
    private CardView followC, messagesC;
    private Dialog reportPopup, reportSentPopup, unfollowDialog;

    private String username;
    private Teacher teacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_teacher);

        getUsernameFromIntent();
        initiateComponents();

    }

    private void getUsernameFromIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }
    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Back button
                backIV = findViewById(R.id.backIV);

                // Report Button
                reportIV = findViewById(R.id.reportIV);

                // Teacher
                teacher = HelpingFunctions.getDisplayedTeacher(username, MainPageS.student.getUsername());

                // Toolbar username
                final TextView toolbarUsernameTV = findViewById(R.id.toolbarUsernameTV);

                // Profile picture
                final ImageView profilePictureIV = findViewById(R.id.profilePictureIV);

                // Name
                final TextView nameTV = findViewById(R.id.nameTV);

                // University
                final TextView universityTV = findViewById(R.id.universityTV);

                // Follow/unfollow status
                followC = findViewById(R.id.followC);
                followIV = findViewById(R.id.followIV);

                // Followers
                followersTV = findViewById(R.id.followersTV);

                // Description
                final TextView descriptionTV = findViewById(R.id.descriptionTV);

                // List View
                subjectsLV = findViewById(R.id.subjectsLV);

                // Message
                messagesC = findViewById(R.id.messagesC);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Toolbar username
                        toolbarUsernameTV.setText(username);

                        // Profile picture
                        if (teacher.getProfileImageBase64().equals("null")) {
                            switch (teacher.getGender()) {
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
                        } else {
                            profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(teacher.getProfileImageBase64()));
                        }

                        // Name
                        if(teacher.getFirstName().equals("null") || teacher.getLastName().equals("null")){
                            nameTV.setText(R.string.message_unknown_name);
                        }else{
                            String name = teacher.getFirstName() + " " + teacher.getLastName();
                            nameTV.setText(name);
                        }

                        // University
                        if(!teacher.getUniversity().equals("null")){
                            universityTV.setText(teacher.getUniversity());
                        }

                        // Follow/unfollow status
                        if (teacher.getFollowingStatus().equals("1")) {
                            // following
                            followC.setCardBackgroundColor(getResources().getColor(R.color.red));
                            followIV.setImageResource(R.drawable.logo_minus);
                            messagesC.setVisibility(View.VISIBLE);
                        } else if (teacher.getFollowingRequestStatus().equals("1")) {
                            // requested
                            followC.setCardBackgroundColor(getResources().getColor(R.color.yellow));
                            followIV.setImageResource(R.drawable.logo_dots);
                        } else {
                            // not following
                            followC.setCardBackgroundColor(getResources().getColor(R.color.green));
                            followIV.setImageResource(R.drawable.logo_plus);
                            if(teacher.getPrivacy().equals("0")){
                                messagesC.setVisibility(View.VISIBLE);
                            }
                        }

                        // Followers
                        followersTV.setText(teacher.getFollowers());

                        // Description
                        if (teacher.getProfileDescription().equals("null")) {
                            descriptionTV.setText(R.string.message_no_description); // No description.
                        } else {
                            descriptionTV.setText(teacher.getProfileDescription());
                        }

                        // Subjects
                        if (teacher.getSubjectNames().size() == 0) {
                            TextView info2TV = findViewById(R.id.info2TV);
                            info2TV.setText(R.string.message_no_subjects);
                        }

                        setListeners();

                    }
                });

            }
        }).start();


    }

    private void setListeners() {

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

                reportPopup = new Dialog(SeeTeacher.this);
                reportPopup.setContentView(R.layout.popup_report);

                ImageView closePopupIV = reportPopup.findViewById(R.id.closePopupIV);
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
                                reportSentPopup = new Dialog(SeeTeacher.this);
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
            }
        });

        // List View
        subjectsAdapter = new SubjectsAdapter(SeeTeacher.this, teacher.getSubjectNames(), teacher.getDomains());
        subjectsLV.setAdapter(subjectsAdapter);
        HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);

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
                intent.putExtra("username_receiver", teacher.getUsername());
                intent.putExtra("blocked", 0);
                intent.putExtra("image", teacher.getProfileImageBase64());
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        followC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final View currentView = v;

                MainPageS.needsRefresh = true;

                // Currently not following
                if (followC.getCardBackgroundColor().getDefaultColor() == getResources().getColor(R.color.green)) {

                    // Public account
                    if (teacher.getPrivacy().equals("0")) {
                        HelpingFunctions.followTeacher(MainPageS.student.getUsername(), username);
                        HelpingFunctions.sendNotificationToTeachers(MainPageS.student.getUsername(), username, "", "", "", "Has started following you.");
                        followC.setCardBackgroundColor(getResources().getColor(R.color.red));
                        followIV.setImageResource(R.drawable.logo_minus);

                        // adjust no. of followers
                        int nr = Integer.parseInt(followersTV.getText().toString()) + 1;
                        String newNumber = nr + "";
                        followersTV.setText(newNumber);
                        teacher.setFollowers(newNumber);

                        Snackbar.make(v, "Following.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Private account
                    String result = HelpingFunctions.requestFollowTeacher(MainPageS.student.getUsername(), username);
                    if(result.equals("Data inserted.")){
                        HelpingFunctions.sendNotificationToTeachers(MainPageS.student.getUsername(), username, "", "", "",  "Has requested to follow you.");
                    }
                    followC.setCardBackgroundColor(getResources().getColor(R.color.yellow));
                    followIV.setImageResource(R.drawable.logo_dots);
                    Snackbar.make(v, "Requested to follow.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Currently following
                if (followC.getCardBackgroundColor().getDefaultColor() == getResources().getColor(R.color.red)) {

                    // Public account
                    if (teacher.getPrivacy().equals("0")) {
                        HelpingFunctions.unfollowTeacher(MainPageS.student.getUsername(), username);
                        followC.setCardBackgroundColor(getResources().getColor(R.color.green));
                        followIV.setImageResource(R.drawable.logo_plus);

                        // adjust no. of followers
                        int nr = Integer.parseInt(followersTV.getText().toString()) - 1;
                        String newNumber = nr + "";
                        followersTV.setText(newNumber);
                        teacher.setFollowers(newNumber);


                        Snackbar.make(v, "Unfollowed.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Private Account
                    unfollowDialog = new Dialog(SeeTeacher.this);
                    unfollowDialog.setContentView(R.layout.popup_unfollow);

                    ImageView closePopupIV = unfollowDialog.findViewById(R.id.closePopupIV);
                    closePopupIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unfollowDialog.dismiss();
                        }
                    });

                    // Remove card background
                    unfollowDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    unfollowDialog.show();


                    Button yesButton = unfollowDialog.findViewById(R.id.yesBttn);
                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HelpingFunctions.unfollowTeacher(MainPageS.student.getUsername(), username);
                            HelpingFunctions.deleteNotification("", "", "", "", "approved your follow request.", username, MainPageS.student.getUsername(), "");
                            followC.setCardBackgroundColor(getResources().getColor(R.color.green));
                            followIV.setImageResource(R.drawable.logo_plus);

                            // adjust no. of followers
                            int nr = Integer.parseInt(followersTV.getText().toString()) - 1;
                            String newNumber = nr + "";
                            followersTV.setText(newNumber);
                            teacher.setFollowers(newNumber);

                            // adjust list
                            subjectsAdapter = new SubjectsAdapter(SeeTeacher.this, teacher.getSubjectNames(), teacher.getDomains());
                            subjectsLV.setAdapter(subjectsAdapter);
                            HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);

                            unfollowDialog.dismiss();
                            Snackbar.make(currentView, "Unfollowed", Snackbar.LENGTH_SHORT).show();

                        }
                    });
                    return;
                }

                // Currently waiting for response
                if (followC.getCardBackgroundColor().getDefaultColor() == getResources().getColor(R.color.yellow)) {
                    HelpingFunctions.requestUnfollowTeacher(MainPageS.student.getUsername(), username);
                    followC.setCardBackgroundColor(getResources().getColor(R.color.green));
                    followIV.setImageResource(R.drawable.logo_plus);
                    Snackbar.make(v, "Request canceled.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*                                   *** A D A P T O R  ***                                   */
    class SubjectsAdapter extends BaseAdapter {

        private ArrayList<String> subjectNames, domainNames;
        private LayoutInflater inflater;



        SubjectsAdapter(Context context, ArrayList<String> subjectNames, ArrayList<String> domainNames) {
            inflater = LayoutInflater.from(context);
            this.subjectNames = subjectNames;
            this.domainNames = domainNames;
        }


        @Override
        public int getCount() {
            return subjectNames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_custom_subject_display, null);

                final TextView subjectTV = convertView.findViewById(R.id.subjectTV);
                TextView domainTV = convertView.findViewById(R.id.domainTV);
                final ImageView endIV = convertView.findViewById(R.id.endIV);


                subjectTV.setText(subjectNames.get(position));
                domainTV.setText(domainNames.get(position));


                if(teacher.getPrivacy().equals("1")){
                    // private account
                    if(followC.getCardBackgroundColor().getDefaultColor() == getResources().getColor(R.color.red)){
                        endIV.setImageResource(R.drawable.ic_navigate_next_black_24dp);
                        endIV.setTag("1");
                    }else {
                        endIV.setImageResource(0);
                        endIV.setTag("0");
                    }
                }else{
                    endIV.setImageResource(R.drawable.ic_navigate_next_black_24dp);
                    endIV.setTag("1");
                }



                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(endIV.getTag().equals("1")){

                            Intent intent = new Intent(SeeTeacher.this, SeeSubject.class);
                            intent.putExtra("teacher_username", username);
                            intent.putExtra("subject", subjectTV.getText().toString());
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);

                        }else{
                            Snackbar.make(v, "You need to follow this user before being able to access their content.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            return convertView;
        }

    }


    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
        SeeTeacher.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}