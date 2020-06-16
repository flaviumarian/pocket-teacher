package com.licence.pocketteacher.student.profile;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.licence.pocketteacher.MessageConversations;
import com.licence.pocketteacher.MessagingPage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.profile.edit_profile.EditProfileS;
import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.student.profile.following.SeeFollowing;
import com.licence.pocketteacher.student.profile.notifications.SeeNotifications;
import com.licence.pocketteacher.student.profile.settings.SettingsS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FragmentProfilesS extends Fragment {


    private View view;
    private ImageView profilePictureIV;
    private CardView editProfileC, followersCard, messagesC;
    private TextView nameTV, universityTV, descriptionTV, followingTV, notificationBadgeTV;
    private Dialog aboutPopup, logOutPopup;
    private Button notificationBttn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_s, container, false);

        setHasOptionsMenu(true);
        initiateComponents();
        setListeners();
        setFields();

        // Clear all notifications
        NotificationManager manager = (NotificationManager) view.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

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
        followingTV = view.findViewById(R.id.followingTV);
        followingTV.setText(HelpingFunctions.getFollowing(MainPageS.student.getUsername()));
        notificationBadgeTV = view.findViewById(R.id.notificationBadgeTV);
        String notificationNumber = HelpingFunctions.getNumberOfNotifications(MainPageS.student.getUsername());
        if(notificationNumber.equals("0")){
            notificationBadgeTV.setVisibility(View.INVISIBLE);
        }else {
            notificationBadgeTV.setVisibility(View.VISIBLE);
            notificationBadgeTV.setText(notificationNumber);
        }

        // Card View
        editProfileC = view.findViewById(R.id.editProfileC);
        followersCard = view.findViewById(R.id.followersCard);
        messagesC = view.findViewById(R.id.messagesC);

        // Button
        notificationBttn = view.findViewById(R.id.notificationBttn);

    }

    private void setListeners(){

        editProfileC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EditProfileS.class);
                startActivityForResult(intent, 0);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        followersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), SeeFollowing.class);
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        messagesC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MessageConversations.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        // Button
        notificationBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), SeeNotifications.class);
                startActivityForResult(intent, 2);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });
    }

    private void setFields(){

        // Profile picture
        if (MainPageS.student.getProfileImageBase64().equals("")) {
            switch (MainPageS.student.getGender()) {
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
            profilePictureIV.setImageBitmap(HelpingFunctions.convertBase64toImage(MainPageS.student.getProfileImageBase64()));
        }


        // Name
        String firstName = MainPageS.student.getFirstName();
        String lastName = MainPageS.student.getLastName();
        if(firstName.equals("null") || lastName.equals("null")){
            nameTV.setText(R.string.message_unknown_name);
        } else{
            String name = firstName + " " + lastName;
            nameTV.setText(name);
        }

        // University
        String university = MainPageS.student.getUniversity();
        if(university.equals("null")){
            universityTV.setText(R.string.message_unknown_university);
        }else{
            universityTV.setText(university);
        }

        // Description
        String description = MainPageS.student.getProfileDescription();
        if(description.equals("null")){
            descriptionTV.setText(R.string.message_no_description);
        }else{
            descriptionTV.setText(description);
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
                Intent intent = new Intent(view.getContext(), SettingsS.class);
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

                logOutPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logOutPopup.show();

                Button yesButton = logOutPopup.findViewById(R.id.yesBttn);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (MainPageS.flag == 0) {
                            // Regular
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
                            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                            sharedPreferencesEditor.putBoolean("loggedIn", false);
                            sharedPreferencesEditor.apply();
                        }

                        if (MainPageS.flag == 1) {
                            // Facebook
                            LoginManager.getInstance().logOut();
                        }
                        if (MainPageS.flag == 2) {
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
            followingTV.setText(HelpingFunctions.getFollowing(MainPageS.student.getUsername()));
            String notificationNumber = HelpingFunctions.getNumberOfNotifications(MainPageS.student.getUsername());
            if(notificationNumber.equals("0")){
                notificationBadgeTV.setVisibility(View.INVISIBLE);
                MainPageS.badgeDrawable.setVisible(false);
            }else {
                notificationBadgeTV.setVisibility(View.VISIBLE);
                notificationBadgeTV.setText(notificationNumber);
                MainPageS.badgeDrawable.setNumber(Integer.parseInt(notificationNumber));
            }
        }

        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            String notificationNumber = HelpingFunctions.getNumberOfNotifications(MainPageS.student.getUsername());
            if(notificationNumber.equals("0")){
                notificationBadgeTV.setVisibility(View.INVISIBLE);
                MainPageS.badgeDrawable.setVisible(false);
            }else {
                notificationBadgeTV.setVisibility(View.VISIBLE);
                notificationBadgeTV.setText(notificationNumber);
                MainPageS.badgeDrawable.setNumber(Integer.parseInt(notificationNumber));
            }
        }
    }


    private void deleteToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String currentToken = instanceIdResult.getToken();
                HelpingFunctions.deleteToken(currentToken, MainPageS.student.getUsername());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Notification Badge
        MainPageS.resetBadge();
    }
}
