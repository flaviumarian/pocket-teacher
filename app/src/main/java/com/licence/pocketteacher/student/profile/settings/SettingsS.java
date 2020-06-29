package com.licence.pocketteacher.student.profile.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.licence.pocketteacher.LoginPage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.miscellaneous.PrivacyPolicy;
import com.licence.pocketteacher.miscellaneous.TermsAndConditions;
import com.licence.pocketteacher.student.MainPageS;

public class SettingsS extends AppCompatActivity {

    private ImageView backIV;
    private ListView change1LV, change2LV, viewLV;
    private CardView deleteAccountC;
    private Button logOutBttn;
    private Dialog logOutPopup, deleteAccountPopup, deletedAccountPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_s);

        initiateComponents();
    }

    private void initiateComponents() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                //Image View
                backIV = findViewById(R.id.backIV);

                // List Views
                change1LV = findViewById(R.id.change1LV);
                change2LV = findViewById(R.id.change2LV);
                viewLV = findViewById(R.id.viewLV);

                // Button
                logOutBttn = findViewById(R.id.logOutBttn);

                // Card View
                deleteAccountC = findViewById(R.id.deleteAccountC);


                // set first List View
                final int[] changesImages = {R.drawable.ic_person_black_24dp, R.drawable.logo_lock};
                final String[] names = {"Username", "Account Privacy"};

                // set second List View
                final int[] changesImages1 = {R.drawable.logo_key_black, R.drawable.ic_email_black_24dp, R.drawable.logo_keyhole};
                final String[] names1 = {"Password", "Email address", "Quick login"};

                // set view settings List View
                final int[] viewImages = {R.drawable.logo_terms_and_conditions, R.drawable.logo_privacy_policy};
                final String[] names3 = {"Terms and Conditions", "Privacy Policy"};


                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // set first List View
                            SettingsS.FirstChangeAdapter firstChangeAdapter = new SettingsS.FirstChangeAdapter(getApplicationContext(), changesImages, names);
                            change1LV.setAdapter(firstChangeAdapter);

                            // set second List View
                            SettingsS.SecondChangeAdapter secondChangeAdapter = new SettingsS.SecondChangeAdapter(getApplicationContext(), changesImages1, names1);
                            change2LV.setAdapter(secondChangeAdapter);

                            // set view settings List View
                            SettingsS.ViewSettingsAdapter viewSettingsAdapter = new SettingsS.ViewSettingsAdapter(getApplicationContext(), viewImages, names3);
                            viewLV.setAdapter(viewSettingsAdapter);


                            setListeners();

                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
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


        // Button
        logOutBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                logOutPopup = new Dialog(SettingsS.this);
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
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // clear all activities but this one
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        // Card View
        deleteAccountC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                deleteAccountPopup = new Dialog(SettingsS.this);
                deleteAccountPopup.setContentView(R.layout.popup_delete_account);

                ImageView closePopupIV = deleteAccountPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAccountPopup.dismiss();
                    }
                });

                // Remove dialog background
                deleteAccountPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteAccountPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                deleteAccountPopup.show();

                Button yesButton = deleteAccountPopup.findViewById(R.id.yesBttn);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAccountPopup.dismiss();

                        // Delete token for this device
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SettingsS.this, new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String currentToken = instanceIdResult.getToken();
                                HelpingFunctions.deleteToken(currentToken, MainPageS.student.getUsername());

                                String result = HelpingFunctions.deleteAccount(MainPageS.student.getUsername());
                                if (result.equals("User deleted.")) {
                                    deletedAccountPopup = new Dialog(SettingsS.this);
                                    deletedAccountPopup.setContentView(R.layout.popup_delete_account_done);

                                    ImageView closePopupIV = deletedAccountPopup.findViewById(R.id.closePopupIV);
                                    closePopupIV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deletedAccountPopup.dismiss();
                                        }
                                    });

                                    // Remove dialog background
                                    deletedAccountPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    deletedAccountPopup.show();

                                    deletedAccountPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {

                                            if(!HelpingFunctions.isConnected(getApplicationContext())){
                                                Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            deleteAccountPopup.dismiss();

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


                                            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // clear all activities but this one
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } else {
                                    // error while deleting -> re-insert the token
                                    FirebaseMessaging.getInstance().subscribeToTopic("pocketTeacher");
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SettingsS.this, new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String newToken = instanceIdResult.getToken();
                                            HelpingFunctions.registerToken(newToken, MainPageS.student.getUsername());

                                        }
                                    });
                                }

                            }
                        });


                    }
                });
            }
        });
    }


    /*                                  *** A D A P T O R S ***                                   */
    // FIRST CHANGES
    class FirstChangeAdapter extends BaseAdapter {

        private int[] images;
        private String[] names;
        private LayoutInflater inflater;


        FirstChangeAdapter(Context context, int[] images, String[] names) {
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
            if (convertView == null) {
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

                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), ChangeUsernameS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), ChangeAccountPrivacyS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                }
            });


            return convertView;
        }
    }

    // FIRST CHANGES
    class SecondChangeAdapter extends BaseAdapter {

        private int[] images;
        private String[] names;
        private LayoutInflater inflater;


        SecondChangeAdapter(Context context, int[] images, String[] names) {
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
            if (convertView == null) {
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

                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), ChangePasswordS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), ChangeEmailS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if (position == 2) {
                        Intent intent = new Intent(getApplicationContext(), ChangeQuickLoginS.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                }
            });


            return convertView;
        }
    }

    // VIEW SETTINGS ADAPTER
    class ViewSettingsAdapter extends BaseAdapter {

        private int[] images;
        private String[] names;
        private LayoutInflater inflater;


        ViewSettingsAdapter(Context context, int[] images, String[] names) {
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
            if (convertView == null) {
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

                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), TermsAndConditions.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                }
            });


            return convertView;
        }
    }


    private void deleteToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SettingsS.this, new OnSuccessListener<InstanceIdResult>() {
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
        SettingsS.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }

}