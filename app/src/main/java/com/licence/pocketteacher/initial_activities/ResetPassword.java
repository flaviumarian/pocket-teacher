package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.miscellaneous.PasswordEncryptDecrypt;

public class ResetPassword extends AppCompatActivity {

    private ImageView backIV;
    private EditText resetCodeET, passwordET, confirmPasswordET;
    private TextView reSendTV, passwordProgressTV,  confirmPasswordProgressTV, middleTV;
    private ProgressBar passwordPB, confirmPasswordPB;
    private GifImageView gifImageView;
    private Button verifyCodeBttn, changeBttn;
    private LinearLayout reSendLayout, newPasswordLayout;
    private View view1, view2;

    private final int HIDE_PASSWORD = 0;
    private final int SHOW_PASSWORD = 1;


    String resetCode, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getLoginIntent();
        initiateComponents();
        setOnClickListeners();
        setUpPasswordListeners();

    }

    private void getLoginIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            resetCode = (String) bundle.get("resetCode");
            email = (String) bundle.get("email");
        }
    }

    private void initiateComponents() {
        // Image View
        backIV = findViewById(R.id.backIV);

        // Gif Image View
        gifImageView = findViewById(R.id.gifImageView);
        ((GifDrawable) gifImageView.getDrawable()).stop(); // initially the gif is stopped

        // Edit Texts
        resetCodeET = findViewById(R.id.resetCodeET);
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);

        // Text Views
        reSendTV = findViewById(R.id.reSendTV);
        passwordProgressTV = findViewById(R.id.passwordProgressTV);
        confirmPasswordProgressTV = findViewById(R.id.confirmPasswordProgressTV);
        middleTV = findViewById(R.id.middleTV);

        // Buttons
        verifyCodeBttn = findViewById(R.id.verifyCodeBttn);
        changeBttn = findViewById(R.id.changeBttn);

        // Linear Layout
        reSendLayout = findViewById(R.id.reSendLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);

        //ProgressBar
        passwordPB = findViewById(R.id.passwordPB);
        confirmPasswordPB = findViewById(R.id.confirmPasswordPB);

        // View
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);

    }

    private void setOnClickListeners() {
        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Text View
        reSendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String subject = "Password Reset Code - NO-REPLY";
                String text = "Dear Pocket Teacher user,\n\n\tA request to change the password has been made, if you do not recognize this action, ignore this message.\nIf you intend to change it, then use the following code in the application page that opened after this request: \n\n\t\t\t" + resetCode + "\n\nThe code becomes invalid once you exit the page.\n\n\n\tBest regards,\n\t\t\tPocket Teacher team";

                HelpingFunctions.sendEmail(email, subject, text);

                TextView reSendTV2 = findViewById(R.id.reSendTV2);
                reSendTV2.setText(R.string.message_reset_password_3);
                reSendTV.setText("");
                reSendTV.setEnabled(false);

                Snackbar.make(v, "Code re-sent.", Snackbar.LENGTH_LONG).show();
            }
        });


        // Buttons
        verifyCodeBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (HelpingFunctions.isEditTextEmpty(resetCodeET)) {
                    resetCodeET.setError("Insert code.");
                    return;
                }

                if (!resetCodeET.getText().toString().equals(resetCode)) {
                    resetCodeET.setError("Incorrect code.");
                    return;
                }

                newPasswordLayout.setVisibility(View.VISIBLE);
                passwordET.setVisibility(View.VISIBLE);
                confirmPasswordET.setVisibility(View.VISIBLE);
                changeBttn.setVisibility(View.VISIBLE);
                resetCodeET.setEnabled(false);
                verifyCodeBttn.setEnabled(false);
                reSendLayout.setEnabled(false);
            }
        });

        changeBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean canRegister = true;

                if(HelpingFunctions.isEditTextEmpty(passwordET)){
                    passwordET.setError("Insert password");
                    canRegister = false;
                }
                if(HelpingFunctions.isEditTextEmpty(confirmPasswordET)){
                    confirmPasswordET.setError("Insert confirm password");
                    canRegister = false;
                }

                if(canRegister && passwordPB.getProgress() >=3 && confirmPasswordPB.getProgress() >= 3){

                    try {
                        if(PasswordEncryptDecrypt.decrypt(HelpingFunctions.getPasswordBasedOnEmail(email)).equals(passwordET.getText().toString())){
                            passwordET.setError("This is your current password.");
                            passwordPB.setProgress(0);
                            passwordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                            passwordProgressTV.setText(R.string.message_password_2);
                            confirmPasswordET.setError("This is your current password.");
                            confirmPasswordPB.setProgress(0);
                            confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.RED));
                            confirmPasswordProgressTV.setText(R.string.message_password_2);
                            return;
                        }


                        String result = HelpingFunctions.setPasswordBasedOnEmail(email, PasswordEncryptDecrypt.encrypt(passwordET.getText().toString()).trim());

                        if(result.equals("Not changed")){
                            Toast.makeText(ResetPassword.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        resetCodeET.setVisibility(View.INVISIBLE);
                        verifyCodeBttn.setVisibility(View.INVISIBLE);
                        reSendLayout.setVisibility(View.INVISIBLE);

                        view1.setVisibility(View.INVISIBLE);
                        view2.setVisibility(View.INVISIBLE);
                        passwordET.setVisibility(View.INVISIBLE);
                        passwordPB.setVisibility(View.INVISIBLE);
                        passwordProgressTV.setVisibility(View.INVISIBLE);

                        confirmPasswordET.setVisibility(View.INVISIBLE);
                        confirmPasswordPB.setVisibility(View.INVISIBLE);
                        confirmPasswordProgressTV.setVisibility(View.INVISIBLE);
                        changeBttn.setVisibility(View.INVISIBLE);

                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int height = size.y;


                        middleTV.setText(R.string.message_password_1);
                        gifImageView.animate().translationYBy(height/3).setDuration(1500);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                ((GifDrawable) gifImageView.getDrawable()).start();
                            }
                        }, 1500);

                        handler.postDelayed(new Runnable() {
                            public void run() {
                                ((GifDrawable) gifImageView.getDrawable()).stop();
                            }
                        }, 5000);

                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /*                                    *** P A S S W O R D S ****                              */
    private void setUpPasswordListeners() {

        // PASSWORD
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordProgressTV.getVisibility() == View.INVISIBLE) {
                    passwordProgressTV.setVisibility(View.VISIBLE);
                    passwordPB.setVisibility(View.VISIBLE);
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_black_24dp, 0);
                    passwordET.setTag(HIDE_PASSWORD);
                    addListenerToEye(passwordET);
                }

                String currentPassword = passwordET.getText().toString();

                if(currentPassword.equals(confirmPasswordET.getText().toString())){
                    int confPassStrength = HelpingFunctions.passwordConditionsFulfilled(confirmPasswordET.getText().toString());
                    if(confPassStrength == 3){
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                        passwordProgressTV.setText(R.string.message_password_3);
                        passwordPB.setProgress(3);
                        confirmPasswordProgressTV.setText(R.string.message_password_7);
                        confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        confirmPasswordPB.setProgress(4);
                        return;
                    }
                    if(confPassStrength == 4){
                        passwordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        passwordProgressTV.setText(R.string.message_password_6);
                        passwordPB.setProgress(4);
                        confirmPasswordProgressTV.setText(R.string.message_password_7);
                        confirmPasswordPB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        confirmPasswordPB.setProgress(4);
                        return;
                    }
                }else {
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
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
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
                    confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_black_24dp, 0);
                    confirmPasswordET.setTag(HIDE_PASSWORD);
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
                    confirmPasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
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
                            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_off_black_24dp, 0);
                            editText.setTag(SHOW_PASSWORD);
                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, R.drawable.ic_visibility_black_24dp, 0);
                            editText.setTag(HIDE_PASSWORD);
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }

                        // Move the cursor to the last character and copying the font for consistency
                        editText.setSelection(editText.length());
                        editText.setTypeface(resetCodeET.getTypeface());

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

        // Hide keyboard
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
        ResetPassword.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
