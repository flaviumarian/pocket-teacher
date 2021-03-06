package com.licence.pocketteacher.student.profile.edit_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;


public class EditProfileS extends AppCompatActivity {

    private ImageView backIV;
    private ListView changeLV;
    private Button logOutBttn;
    private Dialog logOutPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_s);

        initiateComponents();

    }

    private void initiateComponents(){


        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = findViewById(R.id.backIV);

                // List View
                changeLV = findViewById(R.id.changeLV);

                // Button
                logOutBttn = findViewById(R.id.logOutBttn);

                // List View
                final int[] changesImages = {R.drawable.ic_person_black_24dp, R.drawable.ic_wallpaper_black_24dp, R.drawable.logo_description, R.drawable.logo_university, R.drawable.logo_gender};
                final String[] names = {"Name", "Profile Picture", "Description", "University", "Gender"};


                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // List View
                            EditProfileS.ChangeAdapter changeAdapter = new EditProfileS.ChangeAdapter(getApplicationContext(), changesImages, names);
                            changeLV.setAdapter(changeAdapter);

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

        // Button
        logOutBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                logOutPopup = new Dialog(EditProfileS.this);
                logOutPopup.setContentView(R.layout.popup_log_out);

                ImageView closePopupIV = logOutPopup.findViewById(R.id.closePopupIV);
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

                        logOutPopup.dismiss();

                        if (MainPageS.flag == 0) {

                            // Regular
                            SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
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
                        finish();
                    }
                });
            }
        });
    }


    /*                                   *** A D A P T O R  ***                                   */
    class ChangeAdapter extends BaseAdapter {

        private int[] images;
        private String[] names;
        private Context context;
        private LayoutInflater inflater;


        ChangeAdapter(Context context, int[] images, String[] names) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.images = images;
            this.names = names;
        }


        @Override
        public int getCount() {
            return images.length;
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
                convertView = inflater.inflate(R.layout.list_custom_settings, null);

                ImageView startIV = convertView.findViewById(R.id.startIV);
                TextView textView = convertView.findViewById(R.id.textView);
                ImageView endIV = convertView.findViewById(R.id.endIV);

                startIV.setImageResource(images[position]);
                textView.setText(names[position]);
                endIV.setImageResource(R.drawable.ic_navigate_next_black_24dp);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!HelpingFunctions.isConnected(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(position == 0){
                        Intent intent = new Intent(getApplicationContext(), ChangeNameS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if(position == 1){
                        Intent intent = new Intent(getApplicationContext(), ChangeProfilePictureS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if(position == 2){
                        Intent intent = new Intent(getApplicationContext(), ChangeDescriptionS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if(position == 3){
                        Intent intent = new Intent(getApplicationContext(), ChangeUniversityS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if(position == 4){
                        Intent intent = new Intent(getApplicationContext(), ChangeGenderS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                }
            });

            return convertView;
        }
    }



    private void deleteToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( EditProfileS.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String currentToken = instanceIdResult.getToken();
                HelpingFunctions.deleteToken(currentToken, MainPageS.student.getUsername());
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}