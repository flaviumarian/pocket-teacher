package com.licence.pocketteacher.teacher.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.miscellaneous.PasswordEncryptDecrypt;
import com.licence.pocketteacher.teacher.MainPageT;

public class ChangePasswordT extends AppCompatActivity {

    private ImageView backIV;
    private EditText passwordET, confirmPasswordET, helpingET;
    private ProgressBar passwordPB, confirmPasswordPB;
    private TextView passwordProgressTV, confirmPasswordProgressTV;
    private Button clearBttn;

    private final int HIDE_PASSWORD = 0;
    private final int SHOW_PASSWORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_t);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents() {

        // Image View
        backIV = findViewById(R.id.backIV);

        // Edit Texts
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        helpingET = findViewById(R.id.helpingET);

        // Progress Bars
        passwordPB = findViewById(R.id.passwordPB);
        confirmPasswordPB = findViewById(R.id.confirmPasswordPB);

        // Text Views
        passwordProgressTV = findViewById(R.id.passwordProgressTV);
        confirmPasswordProgressTV = findViewById(R.id.confirmPasswordProgressTV);

        // Button
        clearBttn = findViewById(R.id.clearBttn);


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
        clearBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (HelpingFunctions.isEditTextEmpty(passwordET) && HelpingFunctions.isEditTextEmpty(confirmPasswordET)) {
                    return;
                }

                passwordET.setText("");
                passwordPB.setVisibility(View.INVISIBLE);
                passwordProgressTV.setVisibility(View.INVISIBLE);
                passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, 0, 0);


                confirmPasswordET.setText("");
                confirmPasswordPB.setVisibility(View.INVISIBLE);
                confirmPasswordProgressTV.setVisibility(View.INVISIBLE);
                confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, 0, 0);
            }
        });

        // Password
        setUpPasswordListeners();
    }


    /*                                    *** P A S S W O R D S ****                              */
    private void setUpPasswordListeners() {

        // PASSWORD
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordProgressTV.getVisibility() == View.INVISIBLE) {
                    passwordProgressTV.setVisibility(View.VISIBLE);
                    passwordPB.setVisibility(View.VISIBLE);


                    if (passwordET.getTag() != null) {
                        if (passwordET.getTag().equals(0)) {
                            passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_white_24dp, 0);
                        } else {
                            passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_off_white_24dp, 0);
                        }
                    } else {
                        passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_white_24dp, 0);
                        passwordET.setTag(HIDE_PASSWORD);
                    }

                    addListenerToEye(passwordET);
                }

                String currentPassword = passwordET.getText().toString();

                if (currentPassword.equals(confirmPasswordET.getText().toString())) {
                    int confPassStrength = HelpingFunctions.passwordConditionsFulfilled(confirmPasswordET.getText().toString());
                    if (confPassStrength == 3) {
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                        passwordProgressTV.setText(R.string.message_password_3);
                        passwordPB.setProgress(3);
                        confirmPasswordProgressTV.setText(R.string.message_password_7);
                        confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        confirmPasswordPB.setProgress(4);
                        return;
                    }
                    if (confPassStrength == 4) {
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        passwordProgressTV.setText(R.string.message_password_6);
                        passwordPB.setProgress(4);
                        confirmPasswordProgressTV.setText(R.string.message_password_7);
                        confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        confirmPasswordPB.setProgress(4);
                        return;
                    }
                } else {
                    confirmPasswordProgressTV.setText(R.string.message_password_8);
                    confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    confirmPasswordPB.setProgress(1);
                }

                int passStrength;

                if (currentPassword.length() > 10) {
                    passStrength = HelpingFunctions.passwordConditionsFulfilled(currentPassword);
                } else {
                    passStrength = 1;
                }

                passwordPB.setProgress(passStrength);

                switch (passStrength) {
                    case 1:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        passwordProgressTV.setText(R.string.message_password_4);
                        break;
                    case 2:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.rgb(255, 140, 0)));
                        passwordProgressTV.setText(R.string.message_password_5);
                        break;
                    case 3:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                        passwordProgressTV.setText(R.string.message_password_3);
                        break;
                    case 4:
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        passwordProgressTV.setText(R.string.message_password_6);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordET.getText().length() == 0) {
                    passwordProgressTV.setVisibility(View.INVISIBLE);
                    passwordPB.setVisibility(View.INVISIBLE);
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, 0, 0);

                }
            }
        });

        // CONFIRM PASSWORD
        confirmPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (confirmPasswordProgressTV.getVisibility() == View.INVISIBLE) {
                    confirmPasswordProgressTV.setVisibility(View.VISIBLE);
                    confirmPasswordPB.setVisibility(View.VISIBLE);


                    if (confirmPasswordET.getTag() != null) {
                        if (confirmPasswordET.getTag().equals(0)) {
                            confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_white_24dp, 0);
                        } else {
                            confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_off_white_24dp, 0);
                        }
                    } else {
                        confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_white_24dp, 0);
                        confirmPasswordET.setTag(HIDE_PASSWORD);
                    }

                    addListenerToEye(confirmPasswordET);
                }

                if (confirmPasswordET.getText().toString().equals(passwordET.getText().toString()) && passwordPB.getProgress() >= 3) {
                    confirmPasswordProgressTV.setText(R.string.message_password_7);
                    confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    confirmPasswordPB.setProgress(4);
                } else {
                    confirmPasswordProgressTV.setText(R.string.message_password_8);
                    confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    confirmPasswordPB.setProgress(1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (confirmPasswordET.getText().length() == 0) {
                    confirmPasswordProgressTV.setVisibility(View.INVISIBLE);
                    confirmPasswordPB.setVisibility(View.INVISIBLE);
                    confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, 0, 0);

                }
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListenerToEye(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                        if (editText.getTag().equals(0)) {
                            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_off_white_24dp, 0);
                            editText.setTag(SHOW_PASSWORD);
                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.logo_key_white, 0, R.drawable.ic_visibility_white_24dp, 0);
                            editText.setTag(HIDE_PASSWORD);
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }

                        // Move the cursor to the last character and copying the font for consistency
                        editText.setSelection(editText.length());
                        editText.setTypeface(helpingET.getTypeface());

                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (HelpingFunctions.isEditTextEmpty(passwordET) && HelpingFunctions.isEditTextEmpty(confirmPasswordET)) {
            finish();
            ChangePasswordT.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
            return;
        }

        if (passwordPB.getProgress() < 3 || confirmPasswordPB.getProgress() < 4) {
            Snackbar.make(this.getCurrentFocus(), "Invalid password.", Snackbar.LENGTH_INDEFINITE).setAction("Abort", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    ChangePasswordT.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
                }
            }).setActionTextColor(Color.RED).show();
            return;
        }


        String oldPassword = HelpingFunctions.getPasswordBasedOnUsername(MainPageT.teacher.getUsername());
        try {
            oldPassword = PasswordEncryptDecrypt.decrypt(oldPassword);

            if (!oldPassword.equals(passwordET.getText().toString())) {
                HelpingFunctions.setPasswordBasedOnUsername(MainPageT.teacher.getUsername(), PasswordEncryptDecrypt.encrypt(passwordET.getText().toString()).trim());
            } else{
                Snackbar.make(this.getCurrentFocus(), "This is your current password.", Snackbar.LENGTH_LONG).setAction("Abort", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        ChangePasswordT.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
                    }
                }).setActionTextColor(Color.RED).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
        ChangePasswordT.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
