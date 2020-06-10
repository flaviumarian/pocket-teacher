package com.licence.pocketteacher.initial_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;

public class ConfirmationSent extends AppCompatActivity {

    private ImageView backIV;
    boolean fromRegister = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_sent);

        getIntentResults();
        initiateComponents();
        setOnClickListeners();

    }


    private void getIntentResults(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            fromRegister = bundle.getBoolean("fromRegister");
        }
    }

    private void initiateComponents(){

        backIV = findViewById(R.id.backIV);

    }

    private void setOnClickListeners(){

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {

        if(fromRegister){
            finish();
            ConfirmationSent.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        } else{
            Intent intent = new Intent(getApplicationContext(), OpeningPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ConfirmationSent.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
        }
    }
}
