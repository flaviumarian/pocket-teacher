package com.licence.pocketteacher.teacher.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.licence.pocketteacher.messaging.MessageConversations;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.aiding_classes.Subject;
import com.licence.pocketteacher.teacher.profile.edit_profile.EditProfileT;
import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.profile.followers.SeeFollowers;
import com.licence.pocketteacher.teacher.profile.settings.SettingsT;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FragmentProfileT extends Fragment {

    private View view;
    private ImageView profilePictureIV;
    private CardView editProfileC, messagesC, followersCard, newMessagesC;
    private TextView nameTV, universityTV, descriptionTV, followersTV, newMessagesTV;
    private Dialog aboutPopup, logOutPopup;
    private ListView subjectsLV;

    private static int EDIT_PROFILE_CODE = 0;
    private static int SEE_FOLLOWERS_CODE = 1;
    private static int SEE_MESSAGES_CODE = 2;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_t, container, false);

        setHasOptionsMenu(true);
        initiateComponents();
        setFields();

        return view;
    }

    private void initiateComponents(){

        // Toolbar
        Toolbar profileToolbar = view.findViewById(R.id.profileToolbar);
        profileToolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(profileToolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {

                //Image Views
                profilePictureIV = view.findViewById(R.id.profilePictureIV);

                // Text Views
                nameTV = view.findViewById(R.id.nameTV);
                universityTV = view.findViewById(R.id.universityTV);
                descriptionTV = view.findViewById(R.id.descriptionTV);
                followersTV = view.findViewById(R.id.followersTV);
                final String followingNumber = HelpingFunctions.getFollowersNumber(MainPageT.teacher.getUsername());

                newMessagesTV = view.findViewById(R.id.newMessagesTV);
                final String newMessagesCount = HelpingFunctions.getNumberOfUnreadMessages(MainPageT.teacher.getUsername());


                // List View
                subjectsLV = view.findViewById(R.id.subjectsLV);

                // Card View
                editProfileC = view.findViewById(R.id.editProfileC);
                followersCard = view.findViewById(R.id.followersCard);
                messagesC = view.findViewById(R.id.messagesC);
                newMessagesC = view.findViewById(R.id.newMessagesC);

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            followersTV.setText(followingNumber);

                            if(!newMessagesCount.equals("0")){
                                newMessagesC.setVisibility(View.VISIBLE);
                                if(Integer.parseInt(newMessagesCount) > 10){
                                    newMessagesTV.setText(String.valueOf("10+"));
                                }else{
                                    newMessagesTV.setText(newMessagesCount);
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

        // Card View
        editProfileC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(view.getContext(), EditProfileT.class);
                startActivityForResult(intent, EDIT_PROFILE_CODE);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });
        followersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(view.getContext(), SeeFollowers.class);
                startActivityForResult(intent, SEE_FOLLOWERS_CODE);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        messagesC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(view.getContext(), MessageConversations.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, SEE_MESSAGES_CODE);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });


        // List View
        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayList<String> domainNames = new ArrayList<>();

        if(subjectNames.size() == 0){
            TextView info3TV = view.findViewById(R.id.info3TV);
            info3TV.setVisibility(View.VISIBLE);
        }

        for(Subject subject : MainPageT.teacher.getSubjects()){
            subjectNames.add(subject.getSubjectName());
            domainNames.add(subject.getDomainName());
        }

        final SubjectsAdapter subjectsAdapter = new SubjectsAdapter(view.getContext(), subjectNames, domainNames);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            subjectsLV.setAdapter(subjectsAdapter);
                            HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    // set all fields
    private void setFields(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Profile picture
                final String profileImageBase64 = MainPageT.teacher.getProfileImageBase64();

                // Name
                final String firstName = MainPageT.teacher.getFirstName();
                final String lastName = MainPageT.teacher.getLastName();

                // University
                final String university = MainPageT.teacher.getUniversity();

                // Description
                final String description = MainPageT.teacher.getProfileDescription();

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Profile picture
                            if (profileImageBase64.equals("")) {
                                switch (MainPageT.teacher.getGender()) {
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
                                profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(MainPageT.teacher.getProfileImageBase64()));
                            }


                            // Name
                            if (firstName.equals("null") || lastName.equals("null")) {
                                nameTV.setText(R.string.message_unknown_name);
                            } else {
                                String name = firstName + " " + lastName;
                                nameTV.setText(name);
                            }

                            // University
                            if (university.equals("null")) {
                                universityTV.setText(R.string.message_unknown_university);
                            } else {
                                universityTV.setText(university);
                            }

                            // Description
                            if (description.equals("null")) {
                                descriptionTV.setText(R.string.message_no_description);
                            } else {
                                descriptionTV.setText(description);
                            }

                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
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
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.list_custom_subject_display, null);

                TextView subjectTV = convertView.findViewById(R.id.subjectTV);
                TextView domainTV = convertView.findViewById(R.id.domainTV);
                ImageView endIV = convertView.findViewById(R.id.endIV);

                subjectTV.setText(subjectNames.get(position));
                domainTV.setText(domainNames.get(position));
                endIV.setImageResource(0);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(view, "Go to the File Management tab to see/edit information about this subject.", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            return convertView;
        }
    }



    /*                      *** T O O L B A R    M E N U ***                         */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_page, menu);

        // Setting up menu options
        MenuItem item = menu.getItem(0).getSubMenu().getItem(2);
        SpannableString s = new SpannableString("Log out");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        item = menu.getItem(0).getSubMenu().getItem(0);
        s =new SpannableString("Settings");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        item = menu.getItem(0).getSubMenu().getItem(1);
        s =new SpannableString("About");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ImageView closePopupIV;

        switch (id) {
            case R.id.settings:

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

                Intent intent = new Intent(view.getContext(), SettingsT.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                break;
            case R.id.about:


                aboutPopup = new Dialog(view.getContext());
                aboutPopup.setContentView(R.layout.popup_about);

                closePopupIV = aboutPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aboutPopup.dismiss();
                    }
                });

                // Remove dialog background
                aboutPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                aboutPopup.show();
                break;
            case R.id.logOut:

                if(!HelpingFunctions.isConnected(view.getContext())){
                    Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

                logOutPopup = new Dialog(view.getContext());
                logOutPopup.setContentView(R.layout.popup_log_out);

                closePopupIV = logOutPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logOutPopup.dismiss();
                    }
                });

                // Remove dialog background
                logOutPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logOutPopup.show();

                Button yesButton = logOutPopup.findViewById(R.id.yesBttn);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(view.getContext())){
                            Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (MainPageT.flag == 0) {

                            // Regular
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
                            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                            sharedPreferencesEditor.putBoolean("loggedIn", false);
                            sharedPreferencesEditor.apply();
                        }

                        if (MainPageT.flag == 1) {
                            // Facebook
                            LoginManager.getInstance().logOut();
                        }
                        if (MainPageT.flag == 2) {
                            // Google
                            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                            googleSignInClient.signOut();
                        }

                        // Delete token for this device
                        deleteToken();

                        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); // clear all activities but this one
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_PROFILE_CODE){

            if(!HelpingFunctions.isConnected(view.getContext())){
                Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            // from Edit profile
            setFields();
        }

        if(requestCode == SEE_FOLLOWERS_CODE){

            if(!HelpingFunctions.isConnected(view.getContext())){
                Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            followersTV.setText(HelpingFunctions.getFollowersNumber(MainPageT.teacher.getUsername()));
        }

        if(requestCode == SEE_MESSAGES_CODE){
            if(!HelpingFunctions.isConnected(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            String unreadMessages = HelpingFunctions.getNumberOfUnreadMessages(MainPageT.teacher.getUsername());
            if(!unreadMessages.equals("0")){
                newMessagesC.setVisibility(View.VISIBLE);
                if(Integer.parseInt(unreadMessages) > 10){
                    newMessagesTV.setText(String.valueOf("10+"));
                }else{
                    newMessagesTV.setText(unreadMessages);
                }
            }else{
                newMessagesC.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!HelpingFunctions.isConnected(view.getContext())){
            Toast.makeText(view.getContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Notification Badge
        MainPageT.resetBadge();
    }

    private void deleteToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String currentToken = instanceIdResult.getToken();
                HelpingFunctions.deleteToken(currentToken, MainPageT.teacher.getUsername());
            }
        });
    }
}