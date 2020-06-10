package com.licence.pocketteacher.teacher.profile.settings;

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
import com.licence.pocketteacher.teacher.MainPageT;

public class SettingsT extends AppCompatActivity {

    private ImageView backIV;
    private ListView change1LV, change2LV, viewLV;
    private CardView deleteAccountC;
    private Button logOutBttn;
    private Dialog logOutPopup, deleteAccountPopup, deletedAccountPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_t);


        initiateComponents();
        setListeners();

    }

    private void initiateComponents() {

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
    }

    private void setListeners() {

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // set first List View
        int[] changesImages = {R.drawable.ic_person_black_24dp, R.drawable.logo_lock};
        String[] names = {"Username", "Account Privacy"};

        SettingsT.FirstChangeAdapter firstChangeAdapter = new SettingsT.FirstChangeAdapter(getApplicationContext(), changesImages, names);
        change1LV.setAdapter(firstChangeAdapter);


        // set second List View
        int[] changesImages1 = {R.drawable.logo_key_black, R.drawable.ic_email_black_24dp, R.drawable.logo_keyhole};
        String[] names1 = {"Password", "Email address", "Quick login"};

        SettingsT.SecondChangeAdapter secondChangeAdapter = new SettingsT.SecondChangeAdapter(getApplicationContext(), changesImages1, names1);
        change2LV.setAdapter(secondChangeAdapter);


        // set view settings List View
        int[] viewImages = {R.drawable.logo_terms_and_conditions, R.drawable.logo_privacy_policy};
        String[] names3 = {"Terms and Conditions", "Privacy Policy"};

        SettingsT.ViewSettingsAdapter viewSettingsAdapter = new SettingsT.ViewSettingsAdapter(getApplicationContext(), viewImages, names3);
        viewLV.setAdapter(viewSettingsAdapter);

        // Button
        logOutBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutPopup = new Dialog(SettingsT.this);
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

                        if (MainPageT.flag == 0) {

                            // Regular
                            SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
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
                deleteAccountPopup = new Dialog(SettingsT.this);
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
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SettingsT.this, new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String currentToken = instanceIdResult.getToken();
                                HelpingFunctions.deleteToken(currentToken, MainPageT.teacher.getUsername());

                                String result = HelpingFunctions.deleteAccount(MainPageT.teacher.getUsername());
                                if (result.equals("User deleted.")) {
                                    deletedAccountPopup = new Dialog(SettingsT.this);
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
                                            deleteAccountPopup.dismiss();

                                            if (MainPageT.flag == 0) {

                                                // Regular
                                                SharedPreferences sharedPreferences = getSharedPreferences(OpeningPage.AUTO_LOGIN, 0); // 0 - for private mode
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


                                            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // clear all activities but this one
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } else {
                                    // error while deleting -> re-insert the token
                                    FirebaseMessaging.getInstance().subscribeToTopic("pocketTeacher");
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SettingsT.this, new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String newToken = instanceIdResult.getToken();
                                            HelpingFunctions.registerToken(newToken, MainPageT.teacher.getUsername());

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
                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), ChangeUsernameT.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), ChangeAccountPrivacyT.class);
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
                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), ChangePasswordT.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), ChangeEmailT.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                    if (position == 2) {
                        Intent intent = new Intent(getApplicationContext(), ChangeQuickLoginT.class);
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
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SettingsT.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String currentToken = instanceIdResult.getToken();
                HelpingFunctions.deleteToken(currentToken, MainPageT.teacher.getUsername());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        SettingsT.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
