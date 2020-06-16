package com.licence.pocketteacher.teacher.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
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

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.licence.pocketteacher.MessageConversations;
import com.licence.pocketteacher.MessagingPage;
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
    private CardView editProfileC, followersCard, messagesC;
    private TextView nameTV, universityTV, descriptionTV, followersTV;
    private Dialog aboutPopup, logOutPopup;
    private ListView subjectsLV;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_t, container, false);

        setHasOptionsMenu(true);
        initiateComponents();
        setListeners();
        setFields();

        return view;
    }

    private void initiateComponents(){

        // Toolbar
        Toolbar profileToolbar = view.findViewById(R.id.profileToolbar);
        profileToolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(profileToolbar);


        //Image Views
        profilePictureIV = view.findViewById(R.id.profilePictureIV);

        // Text Views
        nameTV = view.findViewById(R.id.nameTV);
        universityTV = view.findViewById(R.id.universityTV);
        descriptionTV = view.findViewById(R.id.descriptionTV);
        followersTV = view.findViewById(R.id.followersTV);
        followersTV.setText(HelpingFunctions.getFollowers(MainPageT.teacher.getUsername()));

        // List View
        subjectsLV = view.findViewById(R.id.subjectsLV);

        // Card View
        editProfileC = view.findViewById(R.id.editProfileC);
        followersCard = view.findViewById(R.id.followersCard);
        messagesC = view.findViewById(R.id.messagesC);
    }

    private void setListeners(){

        // Card View
        editProfileC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EditProfileT.class);
                startActivityForResult(intent, 0);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });
        followersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), SeeFollowers.class);
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        messagesC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MessageConversations.class);
                intent.putExtra("type", 1);
                startActivity(intent);
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


        SubjectsAdapter subjectsAdapter = new SubjectsAdapter(view.getContext(), subjectNames, domainNames);
        subjectsLV.setAdapter(subjectsAdapter);
        HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);
    }

    // set all fields
    private void setFields(){

        // Profile picture
        if (MainPageT.teacher.getProfileImageBase64().equals("")) {
            switch (MainPageT.teacher.getGender()) {
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
        } else {
            profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(MainPageT.teacher.getProfileImageBase64()));
        }

        // Name
        String firstName = MainPageT.teacher.getFirstName();
        String lastName = MainPageT.teacher.getLastName();
        if(firstName.equals("null") || lastName.equals("null")){
            nameTV.setText(R.string.message_unknown_name);
        } else{
            String name = firstName + " " + lastName;
            nameTV.setText(name);
        }

        // University
        String university = MainPageT.teacher.getUniversity();
        if(university.equals("null")){
            universityTV.setText(R.string.message_unknown_university);
        }else{
            universityTV.setText(university);
        }

        // Description
        String description = MainPageT.teacher.getProfileDescription();
        if(description.equals("null")){
            descriptionTV.setText(R.string.message_no_description);
        }else{
            descriptionTV.setText(description);
        }
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

        if(requestCode == 0){
            // from Edit profile
            setFields();
        }

        if(requestCode == 1){
            followersTV.setText(HelpingFunctions.getFollowers(MainPageT.teacher.getUsername()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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
