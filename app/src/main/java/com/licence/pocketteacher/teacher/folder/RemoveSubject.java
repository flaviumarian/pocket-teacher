package com.licence.pocketteacher.teacher.folder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.aiding_classes.Subject;

import java.util.ArrayList;

public class RemoveSubject extends AppCompatActivity {

    private ImageView backIV, closeWarningIV;
    private ArrayList<String> subjectsToDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_subject);

        initiateComponents();

    }

    private void initiateComponents(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image Views
                backIV = findViewById(R.id.backIV);
                closeWarningIV = findViewById(R.id.closeWarningIV);

                // Array List
                subjectsToDelete = new ArrayList<>();

                // List View
                final ListView subjectsLV = findViewById(R.id.subjectsLV);
                final ArrayList<String> subjectNames = new ArrayList<>();
                final ArrayList<String> domainNames = new ArrayList<>();

                for(Subject subject : MainPageT.teacher.getSubjects()){
                    subjectNames.add(subject.getSubjectName());
                    domainNames.add(subject.getDomainName());
                }

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            SubjectsAdapter subjectsAdapter = new SubjectsAdapter(getApplicationContext(), subjectNames, domainNames);
                            subjectsLV.setAdapter(subjectsAdapter);
                            HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);

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

        // Image Views
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        closeWarningIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardView warningC = findViewById(R.id.warningC);
                warningC.animate().alpha(0).setDuration(300);

            }
        });
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

                final TextView subjectTV = convertView.findViewById(R.id.subjectTV);
                TextView domainTV = convertView.findViewById(R.id.domainTV);
                final ImageView endIV = convertView.findViewById(R.id.endIV);

                subjectTV.setText(subjectNames.get(position));
                domainTV.setText(domainNames.get(position));
                endIV.setImageResource(R.drawable.ic_delete_forever_black_24dp);
                endIV.setTag("0");

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(endIV.getTag().equals("0")){
                            endIV.setImageResource(R.drawable.ic_delete_forever_red_24dp);
                            endIV.setTag("1");
                            subjectsToDelete.add(subjectTV.getText().toString());
                        } else{
                            endIV.setImageResource(R.drawable.ic_delete_forever_black_24dp);
                            endIV.setTag("0");
                            subjectsToDelete.remove(subjectTV.getText().toString());
                        }
                    }
                });
            }

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {

        if(subjectsToDelete.size() > 0){
            String email = MainPageT.teacher.getEmail();
            for(String subject : subjectsToDelete){
                HelpingFunctions.removeSubject(email, subject);
                MainPageT.teacher.removeSubject(subject);
            }
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,  returnIntent);
        }


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
