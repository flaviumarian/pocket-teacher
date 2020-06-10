package com.licence.pocketteacher.teacher.profile.followers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.licence.pocketteacher.R;

public class GoPremium extends AppCompatActivity {

    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_premium);

        initiateComponents();
        setListeners();

    }

    private void initiateComponents(){

        // Image View
        backIV = findViewById(R.id.backIV);
    }

    private void setListeners(){

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
        GoPremium.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
