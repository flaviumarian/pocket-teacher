package com.licence.pocketteacher.teacher.folder.subject.files;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.folder.subject.SubjectPage;

import java.util.ArrayList;

public class FilesPage extends AppCompatActivity {

    private ImageView backIV;
    private TextView filesTV, infoTV;
    private CardView addFileC;
    private ListView filesLV;

    public static String folderName;
    public static ArrayList<String> fileNames, likedStatuses;
    public static ArrayList<Integer> likes, comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_page);

        getSubjectIntent();
        initiateComponents();

    }

    private void getSubjectIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            folderName = bundle.getString("folderName");
        }
    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = findViewById(R.id.backIV);

                // Text View
                final TextView folderNameTV = findViewById(R.id.folderNameTV);
                filesTV = findViewById(R.id.filesTV);
                infoTV = findViewById(R.id.infoTV);

                // Card View
                addFileC = findViewById(R.id.addFileC);

                // List View
                filesLV = findViewById(R.id.filesLV);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            // Text View
                            folderNameTV.setText(folderName);
                            setListeners();

                        }
                    });
                } catch (Exception e) {
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

        // Card View
        addFileC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!HelpingFunctions.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), AddFile.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                // List View
                ArrayList<ArrayList> information = HelpingFunctions.getAllFilesForFolder(MainPageT.teacher.getUsername(), MainPageT.teacher.getUsername(), SubjectPage.subjectName, folderName);

                fileNames = information.get(0);
                likedStatuses = information.get(1);

                            FilesAdapter filesAdapter = new FilesAdapter(FilesPage.this, fileNames, likedStatuses, likes, comments);
                            filesLV.setAdapter(filesAdapter);
                            HelpingFunctions.setListViewHeightBasedOnChildren(filesLV);


                likes = new ArrayList<>();
                comments = new ArrayList<>();

                for (String fileName : fileNames) {
                    likes.add(Integer.parseInt(HelpingFunctions.getLikesForPost(MainPageT.teacher.getUsername(), SubjectPage.subjectName, folderName, fileName)));
                    comments.add(Integer.parseInt(HelpingFunctions.getCommentsForPost(MainPageT.teacher.getUsername(), SubjectPage.subjectName, folderName, fileName)));
                }

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // List View
                            if (fileNames.size() == 0) {
                                infoTV.setVisibility(View.VISIBLE);
                                filesLV.setVisibility(View.INVISIBLE);
                                filesTV.setVisibility(View.INVISIBLE);
                            } else {

                                infoTV.setVisibility(View.INVISIBLE);
                                filesLV.setVisibility(View.VISIBLE);
                                filesTV.setVisibility(View.VISIBLE);
                            }

                            FilesAdapter filesAdapter = new FilesAdapter(FilesPage.this, fileNames, likedStatuses, likes, comments);
                            filesLV.setAdapter(filesAdapter);
                            HelpingFunctions.setListViewHeightBasedOnChildren(filesLV);


                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    /*                                   *** A D A P T O R S  ***                                 */
    class FilesAdapter extends BaseAdapter {

        private ArrayList<String> names, likedStatuses;
        private ArrayList<Integer> likes, comments;
        private LayoutInflater inflater;


        FilesAdapter(Context context, ArrayList<String> names, ArrayList<String> likedStatuses, ArrayList<Integer> likes, ArrayList<Integer> comments) {
            inflater = LayoutInflater.from(context);
            this.names = names;
            this.likedStatuses = likedStatuses;
            this.likes = likes;
            this.comments = comments;
        }


        @Override
        public int getCount() {
            return names.size();
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
                convertView = inflater.inflate(R.layout.list_custom_file_display_in_folder, null);


                final TextView fileTV = convertView.findViewById(R.id.fileTV);
                final ImageView likesIV = convertView.findViewById(R.id.likesIV);
                final TextView likesTV = convertView.findViewById(R.id.likesTV);
                final TextView commentsTV = convertView.findViewById(R.id.commentsTV);

                fileTV.setText(names.get(position));
                if (likedStatuses.get(position).equals("1")) {
                    likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                    likesIV.setTag("1");
                } else {
                    likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    likesIV.setTag("0");
                }
                likesTV.setText(likes.get(position) + "");
                commentsTV.setText(comments.get(position) + "");


                likesIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!HelpingFunctions.isConnected(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (likedStatuses.get(position).equals("1")) {
                            HelpingFunctions.unlikePost(MainPageT.teacher.getUsername(), SubjectPage.subjectName, folderName, fileNames.get(position), MainPageT.teacher.getUsername());
                            likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            likesIV.setTag("0");

                            int likeCount = Integer.parseInt(likesTV.getText().toString());
                            String newCount = (likeCount - 1) + "";
                            likedStatuses.set(position, "");
                            likes.set(position, likeCount - 1);
                            likesTV.setText(newCount);
                        } else {
                            HelpingFunctions.likePost(MainPageT.teacher.getUsername(), SubjectPage.subjectName, folderName, fileNames.get(position), MainPageT.teacher.getUsername());
                            likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                            likesIV.setTag("1");

                            int likeCount = Integer.parseInt(likesTV.getText().toString());
                            String newCount = (likeCount + 1) + "";
                            likedStatuses.set(position, "1");
                            likes.set(position, likeCount + 1);
                            likesTV.setText(newCount);
                        }
                    }
                });


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!HelpingFunctions.isConnected(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(getApplicationContext(), SeePostTeacher.class);
                        intent.putExtra("fileName", fileTV.getText().toString());
                        intent.putExtra("fromNotifications", false);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);

                    }
                });
            }
            return convertView;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

            if (!HelpingFunctions.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Added a new file

            infoTV.setVisibility(View.INVISIBLE);
            filesLV.setVisibility(View.VISIBLE);
            filesTV.setVisibility(View.VISIBLE);


            FilesAdapter filesAdapter = new FilesAdapter(FilesPage.this, fileNames, likedStatuses, likes, comments);
            filesLV.setAdapter(filesAdapter);
            HelpingFunctions.setListViewHeightBasedOnChildren(filesLV);
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            if (!HelpingFunctions.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Deleted a file OR added comments
            if (fileNames.size() > 0) {
                likes.clear();
                comments.clear();

                for (String fileName : fileNames) {
                    likes.add(Integer.parseInt(HelpingFunctions.getLikesForPost(MainPageT.teacher.getUsername(), SubjectPage.subjectName, folderName, fileName)));
                    comments.add(Integer.parseInt(HelpingFunctions.getCommentsForPost(MainPageT.teacher.getUsername(), SubjectPage.subjectName, folderName, fileName)));
                }

                FilesAdapter filesAdapter = new FilesAdapter(FilesPage.this, fileNames, likedStatuses, likes, comments);
                filesLV.setAdapter(filesAdapter);
                HelpingFunctions.setListViewHeightBasedOnChildren(filesLV);

                infoTV.setVisibility(View.INVISIBLE);
                filesLV.setVisibility(View.VISIBLE);
                filesTV.setVisibility(View.VISIBLE);

            } else {

                infoTV.setVisibility(View.VISIBLE);
                filesLV.setVisibility(View.INVISIBLE);
                filesTV.setVisibility(View.INVISIBLE);


                FilesAdapter filesAdapter = new FilesAdapter(FilesPage.this, fileNames, likedStatuses, likes, comments);
                filesLV.setAdapter(filesAdapter);
                HelpingFunctions.setListViewHeightBasedOnChildren(filesLV);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (!HelpingFunctions.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}