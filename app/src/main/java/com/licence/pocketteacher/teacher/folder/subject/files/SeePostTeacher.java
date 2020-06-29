package com.licence.pocketteacher.teacher.folder.subject.files;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.aiding_classes.Comment;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.BottomDeleteCommentDialog;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.folder.subject.SubjectPage;
import com.licence.pocketteacher.teacher.profile.followers.SeeStudent;

import java.util.ArrayList;

public class SeePostTeacher extends AppCompatActivity implements BottomDeleteCommentDialog.BottomDeleteCommentListener {

    private ImageView backIV, likesIV, sendCommentIV;
    private TextView infoTV, likesTV, commentsTV, downloadTV;
    private EditText commentET;
    private NestedScrollView nestedScrollView, nestedCommentsView;
    private ScrollView scrollView;
    private RecyclerView commentsRV;
    private Dialog deletePopup;

    private ArrayList<Comment> comments;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;

    private String fileName, likedStatus, likesNumber, commentsNumber, subjectName, folderName, fileUrl;
    private boolean fromNotifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_post_teacher);

        getSubjectIntent();
        initiateComponents();

    }

    private void getSubjectIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            fileName = bundle.getString("fileName");
            likedStatus = bundle.getString("likedStatus");
            likesNumber = bundle.getString("likes");
            commentsNumber = bundle.getString("comments");
            subjectName = bundle.getString("subjectName");
            folderName = bundle.getString("folderName");
            fromNotifications = bundle.getBoolean("fromNotifications");

            if (subjectName == null) {
                subjectName = SubjectPage.subjectName;
                folderName = FilesPage.folderName;
            }
        }
    }

    private void initiateComponents() {

        // Toolbar
        Toolbar fileToolbar = findViewById(R.id.postToolbar);
        fileToolbar.setTitle("");
        this.setSupportActionBar(fileToolbar);


        // Edit Text
        commentET = findViewById(R.id.commentET);

        // Image Views
        backIV = findViewById(R.id.backIV);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final ImageView profileImageIV = findViewById(R.id.profileImageIV);
                likesIV = findViewById(R.id.likesIV);
                sendCommentIV = findViewById(R.id.sendCommentIV);

                // Text Views declarations
                infoTV = findViewById(R.id.infoTV);
                likesTV = findViewById(R.id.likesTV);
                final TextView fileNameTV = findViewById(R.id.fileNameTV);
                final TextView teacherNameTV = findViewById(R.id.teacherNameTV);
                final TextView teacherUsernameTV = findViewById(R.id.teacherUsernameTV);
                commentsTV = findViewById(R.id.commentsTV);
                final TextView titleTV = findViewById(R.id.titleTV);
                final TextView subjectTV = findViewById(R.id.subjectTV);
                final TextView folderTV = findViewById(R.id.folderTV);
                final TextView descriptionTV = findViewById(R.id.descriptionTV);
                final TextView timeTV = findViewById(R.id.timeTV);
                downloadTV = findViewById(R.id.downloadTV);

                final ArrayList<String> fileInformation = HelpingFunctions.getFileInformation(MainPageT.teacher.getUsername(), subjectName, folderName, fileName);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(MainPageT.teacher.getProfileImageBase64()));

                            if (likedStatus.equals("1")) {
                                likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                                likesIV.setTag("1");
                            } else {
                                likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                likesIV.setTag("0");
                            }

                            fileUrl = fileInformation.get(0);
                            if (fileUrl.equals("null")) {
                                downloadTV.setVisibility(View.INVISIBLE);
                            }

                            fileNameTV.setText(fileName);

                            if (MainPageT.teacher.getFirstName().equals("") || MainPageT.teacher.getLastName().equals("") || MainPageT.teacher.getFirstName().equals("null") || MainPageT.teacher.getLastName().equals("null")) {
                                teacherNameTV.setText(R.string.message_unknown_name);
                            } else {
                                String name = MainPageT.teacher.getFirstName() + " " + MainPageT.teacher.getLastName();
                                teacherNameTV.setText(name);
                            }

                            teacherUsernameTV.setText(MainPageT.teacher.getUsername());

                            likesTV.setText(likesNumber);
                            commentsTV.setText(commentsNumber);
                            titleTV.setText(fileInformation.get(1));
                            subjectTV.setText(subjectName);
                            folderTV.setText(folderName);
                            descriptionTV.setText(fileInformation.get(2));
                            timeTV.setText(fileInformation.get(3));
                        }
                    });
                } catch (Exception e) {
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
                comments = HelpingFunctions.getAllCommentsForPost(MainPageT.teacher.getUsername(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        // Text View
        downloadTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(fileUrl);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    Long reference = downloadManager.enqueue(request);

                    Toast.makeText(SeePostTeacher.this, "Downloading...", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Snackbar.make(getCurrentFocus(), "An error occurred. Please try again later.", Snackbar.LENGTH_SHORT);
                }
            }
        });


        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        likesIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (likesIV.getTag().equals("1")) {
                    HelpingFunctions.unlikePost(MainPageT.teacher.getUsername(), subjectName, folderName, fileName, MainPageT.teacher.getUsername());
                    likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    likesIV.setTag("0");

                    int likeCount = Integer.parseInt(likesNumber) - 1;
                    likesNumber = likeCount + "";
                    likesTV.setText(likesNumber);
                    if (!fromNotifications) {
                        FilesPage.likes.set(FilesPage.fileNames.indexOf(fileName), likeCount);
                        FilesPage.likedStatuses.set(FilesPage.fileNames.indexOf(fileName), "");
                    }

                } else {
                    HelpingFunctions.likePost(MainPageT.teacher.getUsername(), subjectName, folderName, fileName, MainPageT.teacher.getUsername());
                    likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                    likesIV.setTag("1");

                    int likeCount = Integer.parseInt(likesNumber) + 1;
                    likesNumber = likeCount + "";
                    likesTV.setText(likesNumber);
                    if (!fromNotifications) {
                        FilesPage.likes.set(FilesPage.fileNames.indexOf(fileName), likeCount);
                        FilesPage.likedStatuses.set(FilesPage.fileNames.indexOf(fileName), "1");
                    }
                }

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
            }
        });

        sendCommentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (HelpingFunctions.isEditTextEmpty(commentET)) {
                    return;
                }

                HelpingFunctions.commentOnPost(commentET.getText().toString(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, MainPageT.teacher.getUsername());
                HelpingFunctions.sendNotificationTeacherCommented(MainPageT.teacher.getUsername(), subjectName, folderName, fileName, MainPageT.teacher.getUsername() + " commented on a post you commented.");
                Comment newComment = new Comment("1", MainPageT.teacher.getUsername(), MainPageT.teacher.getProfileImageBase64(), MainPageT.teacher.getGender(), commentET.getText().toString(), "0s", HelpingFunctions.getCommentTimestamp(commentET.getText().toString(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, MainPageT.teacher.getUsername()), 0, "");
                comments.add(newComment);
                commentsRecyclerAdapter.notifyDataSetChanged();
                commentET.setText("");
                int newNoComm = Integer.parseInt(commentsTV.getText().toString()) + 1;
                String newNoComments = newNoComm + "";
                commentsNumber = newNoComments;
                commentsTV.setText(newNoComments);
                infoTV.setVisibility(View.INVISIBLE);
                if (!fromNotifications) {
                    FilesPage.comments.set(FilesPage.fileNames.indexOf(fileName), FilesPage.comments.get(FilesPage.fileNames.indexOf(fileName)) + 1);
                }
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);

                // Hide keyboard
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        // Edit Text
        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (commentET.getText().length() > 0) {
                    sendCommentIV.setImageResource(R.drawable.logo_send);
                } else {
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
        if (text.equals("Delete comment.")) {
            commentsRecyclerAdapter.deleteComment(position);

            if (comments.size() == 0) {
                infoTV.setVisibility(View.VISIBLE);
            }
        }
    }


    /*                                   *** A D A P T O R  ***                                   */
    class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

        private ArrayList<Comment> comments;
        private Context context;

        public CommentsRecyclerAdapter(ArrayList<Comment> comments, Context context) {
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
            if (comment.getImageBase64().equals("")) {
                switch (comment.getGender()) {
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
            } else {
                holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(comment.getImageBase64()));
            }


            holder.timeTV.setText(comment.getTimeSince());
            holder.usernameTV.setText(comment.getUsername());
            holder.commentTV.setText(comment.getComment());
            String likes = comment.getLikes() + "";
            holder.likeTV.setText(likes);

            // Like/Unlike comment
            if (comment.getLikedStatus().equals("1")) {
                holder.likeIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                holder.likeIV.setTag("1");
            } else {
                holder.likeIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                holder.likeIV.setTag("0");
            }


            // LISTENERS

            // Profile image
            holder.profileImageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!HelpingFunctions.isConnected(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (comment.getType().equals("0")) {
                        Intent intent = new Intent(context, SeeStudent.class);
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

                    if(!HelpingFunctions.isConnected(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (comment.getType().equals("0")) {
                        Intent intent = new Intent(context, SeeStudent.class);
                        intent.putExtra("username", comment.getUsername());
                        v.getContext().startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                }
            });

            holder.likeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!HelpingFunctions.isConnected(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (holder.likeIV.getTag().equals("0")) {
                        String result = HelpingFunctions.likeComment(comment.getComment(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, comment.getTimestamp(), MainPageT.teacher.getUsername(), comment.getUsername());
                        if (result.equals("Data inserted.")) {
                            if (!MainPageT.teacher.getUsername().equals(comment.getUsername())) {
                                // send notification
                                HelpingFunctions.sendNotification(comment.getUsername(), MainPageT.teacher.getUsername() + " liked your comment.");
                            }
                            holder.likeIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                            holder.likeIV.setTag("1");
                            int numLikes = Integer.parseInt(holder.likeTV.getText().toString()) + 1;
                            String newLikes = numLikes + "";
                            holder.likeTV.setText(newLikes);
                            comments.get(position).setLikes(numLikes);
                            comments.get(position).setLikedStatus("1");
                        }
                    } else {
                        HelpingFunctions.unlikeComment(comment.getComment(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, comment.getTimestamp(), MainPageT.teacher.getUsername(), comment.getUsername());
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

                    if(!HelpingFunctions.isConnected(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (comment.getUsername().equals(MainPageT.teacher.getUsername())) {
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

        public void deleteComment(int position) {
            Comment comment = comments.get(position);
            String result = HelpingFunctions.removeComment(comment.getComment(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, comment.getTimestamp(), comment.getUsername());
            if (result.equals("Success.")) {
                int newCommentsCount = Integer.parseInt(commentsNumber) - 1;
                String newCommentsCountString = newCommentsCount + "";
                commentsTV.setText(newCommentsCountString);
                comments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, comments.size());

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
            } else {
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


    /*                      *** T O O L B A R    M E N U ***                         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_file, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ImageView closePopupIV;

        switch (id) {
            case R.id.delete:

                if(!HelpingFunctions.isConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

                deletePopup = new Dialog(SeePostTeacher.this);
                deletePopup.setContentView(R.layout.popup_delete_file);

                closePopupIV = deletePopup.findViewById(R.id.closePopupIV);
                closePopupIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePopup.dismiss();
                    }
                });

                deletePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deletePopup.show();

                Button yesBttn = deletePopup.findViewById(R.id.yesBttn);

                yesBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // DELETE file
                        if (!fileUrl.equals("null")) {
                            fileUrl = "/home/pockette/public_html/uploads/" + fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                        }
                        String result = HelpingFunctions.removeFile(MainPageT.teacher.getUsername(), subjectName, folderName, fileName, fileUrl);

                        if (result.equals("Error occured.")) {
                            deletePopup.dismiss();
                            return;
                        }

                        if (!fromNotifications) {
                            int position = FilesPage.fileNames.indexOf(fileName);
                            FilesPage.fileNames.remove(position);
                            FilesPage.likes.remove(position);
                            FilesPage.comments.remove(position);
                        }

                        deletePopup.dismiss();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        onBackPressed();

                    }
                });

                break;

        }
        return super.onOptionsItemSelected(item);
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
}