package com.licence.pocketteacher.teacher.folder.subject;

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

import java.util.ArrayList;

public class RemoveFolder extends AppCompatActivity {

    private ImageView backIV, closeWarningIV;
    private ListView foldersLV;
    private ArrayList<String> foldersToDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_folder);

        initiateComponents();

    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image Views
                backIV = findViewById(R.id.backIV);
                closeWarningIV = findViewById(R.id.closeWarningIV);

                // Array List
                foldersToDelete = new ArrayList<>();

                // List View
                foldersLV = findViewById(R.id.foldersLV);

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            FoldersAdapter foldersAdapter = new FoldersAdapter(getApplicationContext(), SubjectPage.folders);
                            foldersLV.setAdapter(foldersAdapter);
                            HelpingFunctions.setListViewHeightBasedOnChildren(foldersLV);

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

        closeWarningIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardView warningC = findViewById(R.id.warningC);
                warningC.animate().alpha(0).setDuration(300);

            }
        });
    }


    /*                                   *** A D A P T O R  ***                                   */
    class FoldersAdapter extends BaseAdapter {

        private ArrayList<String> folders;
        private LayoutInflater inflater;


        FoldersAdapter(Context context, ArrayList<String> folders) {
            inflater = LayoutInflater.from(context);
            this.folders = folders;
        }

        @Override
        public int getCount() {
            return folders.size();
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
                convertView = inflater.inflate(R.layout.list_custom_folder, null);

                final TextView folderTV = convertView.findViewById(R.id.folderTV);
                final ImageView endIV = convertView.findViewById(R.id.endIV);

                folderTV.setText(folders.get(position));
                endIV.setImageResource(R.drawable.ic_delete_forever_black_24dp);
                endIV.setTag("0");

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (endIV.getTag().equals("0")) {
                            endIV.setImageResource(R.drawable.ic_delete_forever_red_24dp);
                            endIV.setTag("1");
                            foldersToDelete.add(folderTV.getText().toString());
                        } else {
                            endIV.setImageResource(R.drawable.ic_delete_forever_black_24dp);
                            endIV.setTag("0");
                            foldersToDelete.remove(folderTV.getText().toString());
                        }
                    }
                });

            }

            return convertView;
        }
    }


    @Override
    public void onBackPressed() {

        if (foldersToDelete.size() > 0) {
            String email = MainPageT.teacher.getEmail();

            for (String folder : foldersToDelete) {
                HelpingFunctions.removeFolder(email, SubjectPage.subjectName, folder);
                SubjectPage.folders.remove(folder);
            }

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
