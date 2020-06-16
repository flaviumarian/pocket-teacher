package com.licence.pocketteacher.student.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Comment;
import com.licence.pocketteacher.miscellaneous.BottomDeleteCommentDialog;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.student.home.SeeStudentStudent;


import java.util.ArrayList;

public class SeePostStudent extends AppCompatActivity implements BottomDeleteCommentDialog.BottomDeleteCommentListener{

    private ImageView backIV, profileImageIV, likesIV, sendCommentIV;
    private TextView teacherNameTV, teacherUsernameTV, likesTV, commentsTV, infoTV, downloadTV;
    private EditText commentET;
    private RecyclerView commentsRV;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private NestedScrollView nestedScrollView, nestedCommentsView;
    private ScrollView scrollView;

    private ArrayList<Comment> comments;

    private String teacherUsername, folder, subject, fileName, likedStatus, likesNumber, commentsNumber, fileUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_post_student);

        getSubjectIntent();
        initiateComponents();

    }

    private void getSubjectIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            teacherUsername = bundle.getString("usernameTeacher");
            folder = bundle.getString("folderName");
            subject = bundle.getString("subject");
            fileName = bundle.getString("fileName");
            likedStatus = bundle.getString("likedStatus");
            likesNumber = bundle.getString("likes");
            commentsNumber = bundle.getString("comments");

        }
    }

    private void initiateComponents(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = findViewById(R.id.backIV);
                sendCommentIV = findViewById(R.id.sendCommentIV);

                // Edit Text
                commentET = findViewById(R.id.commentET);

                // File name toolbar
                final TextView fileNameTV = findViewById(R.id.fileNameTV);

                // teacher profile image
                profileImageIV = findViewById(R.id.profileImageIV);
                final String image = HelpingFunctions.getProfileImageBasedOnUsername(teacherUsername);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (image.equals("")) {
                                switch (HelpingFunctions.getGenderBasedOnUsername(teacherUsername)) {
                                    case "0":
                                        profileImageIV.setImageResource(R.drawable.profile_picture_male);
                                        break;
                                    case "1":
                                        profileImageIV.setImageResource(R.drawable.profile_picture_female);
                                        break;
                                    case "2":
                                        profileImageIV.setImageResource(0);
                                        break;
                                }
                            } else {
                                profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(image));
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

                // teacher name
                teacherNameTV = findViewById(R.id.teacherNameTV);
                final String teacherFirstName = HelpingFunctions.getFirstNameBasedOnUsername(teacherUsername);
                final String teacherLastName = HelpingFunctions.getLastNameBasedOnUsername(teacherUsername);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileNameTV.setText(fileName);

                        if(teacherFirstName.equals("") || teacherLastName.equals("")){
                            teacherNameTV.setText(R.string.message_unknown_name);
                        }else{
                            String nameToDisplay = teacherFirstName + " " + teacherLastName;
                            teacherNameTV.setText(nameToDisplay);
                        }
                    }
                });



                // teacher username
                teacherUsernameTV = findViewById(R.id.teacherUsernameTV);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        teacherUsernameTV.setText(teacherUsername);
                    }
                });


                // FILE INFORMATION
                final ArrayList<String> fileInformation = HelpingFunctions.getFileInformation(teacherUsername, subject, folder, fileName);

                // Download text
                downloadTV = findViewById(R.id.downloadTV);
                fileUrl = fileInformation.get(0);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (fileUrl.equals("null")) {
                                downloadTV.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }


                // Time
                final TextView timeTV = findViewById(R.id.timeTV);

                // Title
                final TextView titleTV = findViewById(R.id.titleTV);

                // Subject
                final TextView subjectTV = findViewById(R.id.subjectTV);

                // Folder
                final TextView folderTV = findViewById(R.id.folderTV);

                // Description
                final TextView descriptionTV = findViewById(R.id.descriptionTV);

                // Liked status
                likesIV = findViewById(R.id.likesIV);

                // Likes number
                likesTV = findViewById(R.id.likesTV);

                // Comments number
                commentsTV = findViewById(R.id.commentsTV);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeTV.setText(fileInformation.get(3));
                            titleTV.setText(fileInformation.get(1));
                            subjectTV.setText(subject);
                            folderTV.setText(folder);
                            descriptionTV.setText(fileInformation.get(2));

                            if (likedStatus.equals("1")) {
                                likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                                likesIV.setTag("1");
                            } else {
                                likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                likesIV.setTag("0");
                            }

                            likesTV.setText(likesNumber);
                            commentsTV.setText(commentsNumber);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }

                // Recycler View
                commentsRV = findViewById(R.id.commentsRV);

                // Nested Scroll View
                nestedScrollView = findViewById(R.id.nestedScrollView);
                nestedCommentsView = findViewById(R.id.nestedCommentsView);

                // Scroll View
                scrollView = findViewById(R.id.scrollView);


                // Comments
                infoTV = findViewById(R.id.infoTV);
                comments = HelpingFunctions.getAllCommentsForPost(teacherUsername, MainPageS.student.getUsername(), subject, folder, fileName);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (comments.size() > 0) {
                                infoTV.setVisibility(View.INVISIBLE);
                            } else {
                                infoTV.setVisibility(View.VISIBLE);
                            }

                            setListeners();

                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        }).start();


    }

    // set all listeners
    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(){

        // Text View
        downloadTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(fileUrl);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    //Setting description of request
                    request.setDescription("Your file is downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    Long reference = downloadManager.enqueue(request);

                    Toast.makeText(SeePostStudent.this, "Downloading...", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Snackbar.make(getCurrentFocus(), "An error occurred. Please try again later.", Snackbar.LENGTH_SHORT);
                }
            }
        });

        teacherNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeeTeacher.class);
                intent.putExtra("username", teacherUsername);
                v.getContext().startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        teacherUsernameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeeTeacher.class);
                intent.putExtra("username", teacherUsername);
                v.getContext().startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });


        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profileImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeeTeacher.class);
                intent.putExtra("username", teacherUsername);
                v.getContext().startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });

        likesIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likesIV.getTag().equals("1")){
                    HelpingFunctions.unlikePost(teacherUsername, subject, folder, fileName, MainPageS.student.getUsername());
                    likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    likesIV.setTag("0");

                    int likeCount = Integer.parseInt(likesNumber)  - 1;
                    likesNumber = likeCount + "";
                    likesTV.setText(likesNumber);

                }else{

                    String result = HelpingFunctions.likePost(teacherUsername, subject, folder, fileName, MainPageS.student.getUsername());
                    if(result.equals("Data inserted.")){
                        // Send notification
                        HelpingFunctions.sendNotification(teacherUsername, MainPageS.student.getUsername() + " liked your post.");
                        likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                        likesIV.setTag("1");

                        int likeCount = Integer.parseInt(likesNumber)  + 1;
                        likesNumber = likeCount + "";
                        likesTV.setText(likesNumber);
                    }
                }

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,  returnIntent);
            }
        });

        sendCommentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HelpingFunctions.isEditTextEmpty(commentET)){
                    return;
                }

                HelpingFunctions.commentOnPost(commentET.getText().toString(), teacherUsername, subject, folder, fileName, MainPageS.student.getUsername());
                HelpingFunctions.sendNotification(teacherUsername, MainPageS.student.getUsername() + " commented on your post.");
                HelpingFunctions.sendNotificationStudentCommented(MainPageS.student.getUsername(), teacherUsername, subject, folder, fileName, MainPageS.student.getUsername() + " commented on a post you commented.");
                Comment newComment = new Comment("0", MainPageS.student.getUsername(), MainPageS.student.getProfileImageBase64(), MainPageS.student.getGender(), commentET.getText().toString(), "0s", HelpingFunctions.getCommentTimestamp(commentET.getText().toString(), teacherUsername, subject, folder, fileName, MainPageS.student.getUsername()),0, "");
                comments.add(newComment);
                commentsRecyclerAdapter.notifyDataSetChanged();
                commentET.setText("");
                int newNoComm = Integer.parseInt(commentsTV.getText().toString()) + 1;
                String newNoComments = newNoComm + "";
                commentsNumber = newNoComments;
                commentsTV.setText(newNoComments);
                infoTV.setVisibility(View.INVISIBLE);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,  returnIntent);

                // Hide keyboard
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        // Edit Text
        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(commentET.getText().length() > 0){
                    sendCommentIV.setImageResource(R.drawable.logo_send);
                }else{
                    sendCommentIV.setImageResource(R.drawable.logo_send_grey);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // only 8 rows allowed
                if (commentET.getLayout().getLineCount() > 5) {
                    commentET.getText().delete(commentET.getText().length() - 1, commentET.getText().length());
                }
            }
        });

        // Nested Scroll view
        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.scrollView).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        nestedCommentsView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        // Scroll View
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        // Recycler View
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(comments, getApplicationContext());

        commentsRV.setAdapter(commentsRecyclerAdapter);
        commentsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    @Override
    public void onButtonClicked(String text, int position) {
        if(text.equals("Delete comment.")) {
            commentsRecyclerAdapter.deleteComment(position);

            if(comments.size() == 0){
                infoTV.setVisibility(View.VISIBLE);
            }
        }
    }


    /*                                   *** A D A P T O R  ***                                   */
    class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

        private ArrayList<Comment> comments;
        private Context context;


        public CommentsRecyclerAdapter(ArrayList<Comment> comments, Context context){
            this.comments = comments;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_custom_comment, parent, false);
            ViewHolder holder = new ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final Comment comment = comments.get(position);

            // Profile image
            if(comment.getImageBase64().equals("")){
                switch(comment.getGender()){
                    case "0":
                        holder.profileImageIV.setImageResource(R.drawable.profile_picture_male);
                        break;
                    case "1":
                        holder.profileImageIV.setImageResource(R.drawable.profile_picture_female);
                        break;
                    case "2":
                        holder.profileImageIV.setImageResource(0);
                        break;
                }
            }else{
                holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(comment.getImageBase64()));
            }


            holder.timeTV.setText(comment.getTimeSince());
            holder.usernameTV.setText(comment.getUsername());
            holder.commentTV.setText(comment.getComment());
            String likes = comment.getLikes() + "";
            holder.likeTV.setText(likes);

            if(comment.getLikedStatus().equals("1")){
                holder.likeIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                holder.likeIV.setTag("1");
            }else{
                holder.likeIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                holder.likeIV.setTag("0");
            }


            // LISTENERS

            // Profile image
            holder.profileImageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(comment.getType().equals("0")){
                        if(comment.getUsername().equals(MainPageS.student.getUsername())){
                            return;
                        }
                        Intent intent = new Intent(context, SeeStudentStudent.class);
                        intent.putExtra("username", comment.getUsername());
                        v.getContext().startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }else{
                        Intent intent = new Intent(context, SeeTeacher.class);
                        intent.putExtra("username", comment.getUsername());
                        v.getContext().startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                }
            });

            // Userame
            holder.usernameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(comment.getType().equals("0")){
                        if(comment.getUsername().equals(MainPageS.student.getUsername())){
                            return;
                        }
                        Intent intent = new Intent(context, SeeStudentStudent.class);
                        intent.putExtra("username", comment.getUsername());
                        v.getContext().startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }else{
                        Intent intent = new Intent(context, SeeTeacher.class);
                        intent.putExtra("username", comment.getUsername());
                        v.getContext().startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                }
            });

            // Like/Unlike comment
            holder.likeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.likeIV.getTag().equals("0")){
                        String result = HelpingFunctions.likeComment(comment.getComment(), teacherUsername, subject, folder, fileName, comment.getTimestamp(), MainPageS.student.getUsername(), comment.getUsername());
                        if(result.equals("Data inserted.")){
                            if(!MainPageS.student.getUsername().equals(comment.getUsername())){
                                // send notification
                                HelpingFunctions.sendNotification(comment.getUsername(), MainPageS.student.getUsername() + " liked your comment.");
                            }

                            holder.likeIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                            holder.likeIV.setTag("1");
                            int numLikes = Integer.parseInt(holder.likeTV.getText().toString()) + 1;
                            String newLikes = numLikes + "";
                            holder.likeTV.setText(newLikes);
                            comments.get(position).setLikes(numLikes);
                            comments.get(position).setLikedStatus("1");
                        }




                    }else{
                        HelpingFunctions.unlikeComment(comment.getComment(), teacherUsername, subject, folder, fileName, comment.getTimestamp(), MainPageS.student.getUsername(), comment.getUsername());
                        holder.likeIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        holder.likeIV.setTag("0");
                        int numLikes = Integer.parseInt(holder.likeTV.getText().toString()) - 1;
                        String newLikes = numLikes + "";
                        holder.likeTV.setText(newLikes);
                        comments.get(position).setLikes(numLikes);
                        comments.get(position).setLikedStatus("0");
                    }
                }
            });

            // Delete comment
            holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(comment.getUsername().equals(MainPageS.student.getUsername())){
                        // apare optiune sa sterg comentariul
                        BottomDeleteCommentDialog bottomDeleteCommentDialog = new BottomDeleteCommentDialog(position);
                        bottomDeleteCommentDialog.show(getSupportFragmentManager(), "DELETE_COMMENT");

                        return true;
                    }
                    return true;
                }
            });


        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        public void deleteComment(int position){
            Comment comment = comments.get(position);
            String result = HelpingFunctions.removeComment(comment.getComment(), teacherUsername, subject, folder, fileName, comment.getTimestamp(), comment.getUsername());
            if(result.equals("Success.")){
                int newCommentsCount = Integer.parseInt(commentsNumber) - 1;
                String newCommentsCountString = newCommentsCount + "";
                commentsTV.setText(newCommentsCountString);
                comments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,comments.size());
            }else{
                Toast.makeText(context, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView profileImageIV;
            TextView timeTV;
            TextView usernameTV;
            TextView commentTV;
            ImageView likeIV;
            TextView likeTV;
            RelativeLayout relativeLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                profileImageIV = itemView.findViewById(R.id.profileImageIV);
                timeTV = itemView.findViewById(R.id.timeTV);
                usernameTV = itemView.findViewById(R.id.usernameTV);
                commentTV = itemView.findViewById(R.id.commentTV);
                likeIV = itemView.findViewById(R.id.likeIV);
                likeTV = itemView.findViewById(R.id.likeTV);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
            }
        }
    }


    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
