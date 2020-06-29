package com.licence.pocketteacher.teacher.updates.follow_requests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.aiding_classes.Student;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.adapters.StudentsRecyclerAdapter;

import java.util.ArrayList;

public class SeeFollowRequests extends AppCompatActivity {

    private ImageView backIV;
    private RecyclerView followRequestsRV;

    private ArrayList<Student> requestingStudents;
    private StudentsRecyclerAdapter studentsRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_follow_requests);

        initiateComponents();

    }

    private void initiateComponents(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = findViewById(R.id.backIV);

                // Recycler View
                followRequestsRV= findViewById(R.id.followRequestsRV);
                requestingStudents = HelpingFunctions.getAllFollowingRequestStudents(MainPageT.teacher.getUsername());

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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


        // Recycler View
        studentsRecyclerAdapter = new StudentsRecyclerAdapter(requestingStudents, SeeFollowRequests.this, getWindow().getDecorView().findViewById(android.R.id.content));

        followRequestsRV.setAdapter(studentsRecyclerAdapter);
        followRequestsRV.setLayoutManager(new LinearLayoutManager(SeeFollowRequests.this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToApproveOrDenyRequest(studentsRecyclerAdapter));
        itemTouchHelper.attachToRecyclerView(followRequestsRV);

    }


    @Override
    public void onBackPressed() {

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(!HelpingFunctions.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                requestingStudents = HelpingFunctions.getAllFollowingRequestStudents(MainPageT.teacher.getUsername());


                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                studentsRecyclerAdapter.setStudents(requestingStudents);
                                studentsRecyclerAdapter.notifyDataSetChanged();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();



    }
}