package com.licence.pocketteacher.student.search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.aiding_classes.Subject;

import java.util.ArrayList;
import java.util.Collections;

public class SeeSubject extends AppCompatActivity {

    private ImageView backIV;
    private TextView infoTV, info1TV, foldersTV, postsTV;
    private ListView foldersLV, postsLV;
    private FoldersAdapter foldersAdapter;
    private PostsAdapter postsAdapter;
    private Dialog subjectDetailsPopup, domainDetailsPopup, reportPopup, reportSentPopup;


    private String usernameTeacher, subject;
    private static ArrayList<String> folders;
    public static ArrayList<String> fileNames, likedStatuses;
    public static ArrayList<Integer> likes, comments;

    private static int currentPosition = -1;
    private static String currentFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_subject);

        getSubjectFromIntent();
        initiateComponents();
        setListeners();

    }

    private void getSubjectFromIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            usernameTeacher = bundle.getString("teacher_username");
            subject = bundle.getString("subject");
        }
    }

    private void initiateComponents(){

        // Toolbar
        Toolbar subjectToolbar = findViewById(R.id.subjectToolbar);
        subjectToolbar.setTitle("");
        this.setSupportActionBar(subjectToolbar);

        // Image View
        backIV = findViewById(R.id.backIV);
        ImageView subjectLogoIV = findViewById(R.id.subjectLogoIV);
        subjectLogoIV.setImageBitmap(HelpingFunctions.convertBase64toImage(HelpingFunctions.getSubjectImage(subject)));

        // Text Views
        infoTV = findViewById(R.id.infoTV);
        foldersTV = findViewById(R.id.foldersTV);
        postsTV = findViewById(R.id.postsTV);
        info1TV = findViewById(R.id.info1TV);

        // Toolbar textview
        TextView subjectNameTV = findViewById(R.id.subjectNameTV);
        subjectNameTV.setText(subject);


        // Folders
        folders = HelpingFunctions.getFoldersForSubjectAndTeacher(usernameTeacher, subject);
        Collections.sort(folders);


        // List View
        foldersLV = findViewById(R.id.foldersLV);
        postsLV = findViewById(R.id.postsLV);

    }

    private void setListeners(){

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // List View
        if (folders.size() == 0) {
            infoTV.setVisibility(View.VISIBLE);
            foldersTV.setVisibility(View.INVISIBLE);
            postsTV.setVisibility(View.INVISIBLE);
        } else {
            infoTV.setVisibility(View.INVISIBLE);
            foldersTV.setVisibility(View.VISIBLE);
        }

        foldersAdapter = new FoldersAdapter(SeeSubject.this, folders);
        foldersLV.setAdapter(foldersAdapter);
        foldersAdapter.notifyDataSetChanged();
        HelpingFunctions.setListViewHeightBasedOnChildren(foldersLV);
    }

    private boolean getAllPostsForFolder(String folderName){

        ArrayList<ArrayList> information = HelpingFunctions.getAllFilesForFolder(MainPageS.student.getUsername(), usernameTeacher, subject, folderName);
        fileNames = information.get(1);
        likedStatuses = information.get(3);

        if (fileNames.size() == 0) {
            postsTV.setVisibility(View.INVISIBLE);
            postsAdapter = new PostsAdapter(SeeSubject.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Integer>(), new ArrayList<Integer>());
            postsLV.setAdapter(postsAdapter);
            HelpingFunctions.setListViewHeightBasedOnChildren(postsLV);
            return false;
        } else {
            postsTV.setVisibility(View.VISIBLE);
        }

        likes = new ArrayList<>();
        comments = new ArrayList<>();
        for (String fileName : fileNames) {
            likes.add(Integer.parseInt(HelpingFunctions.getLikesForPost(usernameTeacher, subject, folderName, fileName)));
            comments.add(Integer.parseInt(HelpingFunctions.getCommentsForPost(usernameTeacher, subject, folderName, fileName)));
        }

        postsAdapter = new PostsAdapter(SeeSubject.this, fileNames, likedStatuses, likes, comments);
        postsLV.setAdapter(postsAdapter);
        postsAdapter.notifyDataSetChanged();
        HelpingFunctions.setListViewHeightBasedOnChildren(postsLV);

        return true;
    }


    /*                                   *** A D A P T O R S  ***                                 */
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

                if(currentPosition == position){
                    endIV.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    endIV.setTag("1");
                }else {
                    endIV.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    endIV.setTag("0");
                }


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(endIV.getTag().equals("1")){
                            endIV.setImageResource(R.drawable.ic_expand_more_black_24dp);
                            endIV.setTag("0");
                            currentPosition = -1;
                            postsTV.setVisibility(View.INVISIBLE);
                            info1TV.setVisibility(View.INVISIBLE);

                            postsAdapter = new PostsAdapter(SeeSubject.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Integer>(), new ArrayList<Integer>());
                            postsLV.setAdapter(postsAdapter);
                            HelpingFunctions.setListViewHeightBasedOnChildren(postsLV);

                            return;
                        }

                        if(getAllPostsForFolder(folderTV.getText().toString())){
                           endIV.setImageResource(R.drawable.ic_expand_less_black_24dp);
                           endIV.setTag("1");
                           currentPosition = position;
                           currentFolder = folderTV.getText().toString();
                           info1TV.setVisibility(View.INVISIBLE);
                        }else{
                            // no posts
                            endIV.setImageResource(R.drawable.ic_expand_less_black_24dp);
                            endIV.setTag("1");
                            currentPosition = position;
                            info1TV.setVisibility(View.VISIBLE);
                        }

                        foldersAdapter = new FoldersAdapter(SeeSubject.this, folders);
                        foldersLV.setAdapter(foldersAdapter);
                        foldersAdapter.notifyDataSetChanged();
                        HelpingFunctions.setListViewHeightBasedOnChildren(foldersLV);

                    }
                });
            }
            return convertView;
        }

    }

    class PostsAdapter extends BaseAdapter {

        private ArrayList<String> names, likedStatuses;
        private ArrayList<Integer> likes, comments;
        private Context context;
        private LayoutInflater inflater;


        PostsAdapter(Context context, ArrayList<String> names, ArrayList<String> likedStatuses, ArrayList<Integer> likes, ArrayList<Integer> comments) {
            this.context = context;
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

                // Set up like-unlike system
                likesIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (likedStatuses.get(position).equals("1")) {
                            HelpingFunctions.unlikePost(usernameTeacher, subject, currentFolder, fileNames.get(position), MainPageS.student.getUsername());
                            likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            likesIV.setTag("0");

                            int likeCount = Integer.parseInt(likesTV.getText().toString());
                            String newCount = (likeCount - 1) + "";
                            likedStatuses.set(position, "");
                            likes.set(position, likeCount - 1);
                            likesTV.setText(newCount);
                        } else {
                            HelpingFunctions.likePost(usernameTeacher, subject, currentFolder, fileNames.get(position), MainPageS.student.getUsername());
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

                        final ProgressDialog loading = ProgressDialog.show(SeeSubject.this, "Please wait", "Loading...", true);
                        new Thread() {
                            @Override
                            public void run() {

                                String likedStatus;
                                if (likesIV.getTag().equals("0")) {
                                    likedStatus = "";
                                } else {
                                    likedStatus = "1";
                                }

                                Intent intent = new Intent(getApplicationContext(), SeePostStudent.class);
                                intent.putExtra("usernameTeacher", usernameTeacher);
                                intent.putExtra("folderName", currentFolder);
                                intent.putExtra("subject", subject);
                                intent.putExtra("fileName", fileTV.getText().toString());
                                intent.putExtra("likedStatus", likedStatus);
                                intent.putExtra("likes", likesTV.getText().toString());
                                intent.putExtra("comments", commentsTV.getText().toString());
                                startActivityForResult(intent, 1);
                                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);


                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.dismiss();
                                        }
                                    });
                                } catch (final Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
            }
            return convertView;
        }
    }

    class DomainAdapter extends BaseAdapter {

        private ArrayList<String> subjects;
        private LayoutInflater inflater;


        DomainAdapter(Context context, ArrayList<String> subjects) {

            inflater = LayoutInflater.from(context);
            this.subjects = subjects;
        }

        @Override
        public int getCount() {
            return subjects.size();
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
                ImageView endIV = convertView.findViewById(R.id.endIV);

                folderTV.setText(subjects.get(position));
                endIV.setImageResource(0);
            }

            return convertView;
        }
    }



    /*                      *** T O O L B A R    M E N U ***                         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject, menu);

        // Setting up menu options
        MenuItem item = menu.getItem(0).getSubMenu().getItem(1);
        SpannableString s = new SpannableString("Report");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        item = menu.getItem(0).getSubMenu().getItem(0);
        s = new SpannableString("Details");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ImageView closePopupIV;

        switch (id) {
            case R.id.details:
                subjectDetailsPopup = new Dialog(SeeSubject.this);
                subjectDetailsPopup.setContentView(R.layout.popup_subject_information);

                closePopupIV = subjectDetailsPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subjectDetailsPopup.dismiss();
                    }
                });

                // Remove dialog background
                subjectDetailsPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                subjectDetailsPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                subjectDetailsPopup.show();

                ImageView popupSubjectLogoIV = subjectDetailsPopup.findViewById(R.id.popupSubjectLogoIV);
                TextView popupSubjectNameTV = subjectDetailsPopup.findViewById(R.id.popupSubjectNameTV);
                TextView popupDomainNameTV = subjectDetailsPopup.findViewById(R.id.popupDomainNameTV);
                ImageView popupDomainInfoIV = subjectDetailsPopup.findViewById(R.id.popupDomainInfoIV);
                TextView popupDescriptionTV = subjectDetailsPopup.findViewById(R.id.popupDescriptionTV);

                final Subject infoSubject = HelpingFunctions.getFullSubjectDetails(subject);

                popupSubjectLogoIV.setImageBitmap(HelpingFunctions.convertBase64toImage(infoSubject.getImage()));
                popupSubjectNameTV.setText(infoSubject.getSubjectName());
                popupDomainNameTV.setText(infoSubject.getDomainName());
                popupDescriptionTV.setText(infoSubject.getSubjectDescription());

                popupDomainInfoIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        subjectDetailsPopup.dismiss();

                        // Domain information
                        domainDetailsPopup = new Dialog(SeeSubject.this);
                        domainDetailsPopup.setContentView(R.layout.popup_domain_information);

                        ImageView closePopupIV = domainDetailsPopup.findViewById(R.id.closePopupIV);
                        closePopupIV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                domainDetailsPopup.dismiss();
                            }
                        });

                        // Remove dialog background
                        domainDetailsPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        domainDetailsPopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        domainDetailsPopup.show();

                        ImageView popupDomainLogoIV = domainDetailsPopup.findViewById(R.id.popupDomainLogoIV);
                        TextView popupDomainNameTV = domainDetailsPopup.findViewById(R.id.popupDomainNameTV);
                        TextView popupDomainDescriptionTV = domainDetailsPopup.findViewById(R.id.popupDomainDescriptionTV);
                        ListView subjectsLV = domainDetailsPopup.findViewById(R.id.subjectsLV);

                        // information
                        popupDomainNameTV.setText(infoSubject.getDomainName());
                        popupDomainDescriptionTV.setText(infoSubject.getDomainDescription());
                        popupDomainLogoIV.setImageBitmap(HelpingFunctions.convertBase64toImage(infoSubject.getDomainImage()));

                        // list
                        ArrayList<ArrayList> information2 = HelpingFunctions.getSubjectsForDomain(infoSubject.getDomainName());
                        Collections.sort(information2.get(0));
                        DomainAdapter domainAdapter = new DomainAdapter(SeeSubject.this, information2.get(0));
                        subjectsLV.setAdapter(domainAdapter);
                        HelpingFunctions.setListViewHeightBasedOnChildren(subjectsLV);

                    }
                });


                break;
            case R.id.report:
                reportPopup = new Dialog(SeeSubject.this);
                reportPopup.setContentView(R.layout.popup_report);

                closePopupIV = reportPopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportPopup.dismiss();
                    }
                });

                // Remove dialog background
                reportPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportPopup.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                reportPopup.show();

                final EditText titleET = reportPopup.findViewById(R.id.titleET);
                final EditText messageET = reportPopup.findViewById(R.id.messageET);
                Button sendBttn = reportPopup.findViewById(R.id.sendBttn);

                sendBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean canSend = true;

                        if (HelpingFunctions.isEditTextEmpty(titleET)) {
                            titleET.setError("Insert title");
                            canSend = false;
                        }
                        if (HelpingFunctions.isEditTextEmpty(messageET)) {
                            messageET.setError("Insert message");
                            canSend = false;
                        }

                        if (canSend) {

                            String result = HelpingFunctions.sendReportStudent(MainPageS.student.getEmail(), titleET.getText().toString(), messageET.getText().toString());
                            reportPopup.dismiss();
                            if (result.equals("Report sent.")) {
                                reportSentPopup = new Dialog(SeeSubject.this);
                                reportSentPopup.setContentView(R.layout.popup_report_sent);

                                ImageView closePopupIV = reportSentPopup.findViewById(R.id.closePopupIV);
                                closePopupIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reportSentPopup.dismiss();

                                    }
                                });

                                // Remove dialog background
                                reportSentPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                reportSentPopup.show();
                            }

                        }
                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){

            currentPosition = -1;
            foldersAdapter = new FoldersAdapter(SeeSubject.this, folders);
            foldersLV.setAdapter(foldersAdapter);
            foldersAdapter.notifyDataSetChanged();
            HelpingFunctions.setListViewHeightBasedOnChildren(foldersLV);

            getAllPostsForFolder("");
        }
    }

    @Override
    public void onBackPressed() {


        finish();
        SeeSubject.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
