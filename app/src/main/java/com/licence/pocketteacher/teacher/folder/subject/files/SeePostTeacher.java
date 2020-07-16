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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.aiding_classes.Comment;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.BottomDeleteCommentDialog;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.home.LoadMore;
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

    private ArrayList<Comment> displayedComments;
    private CommentsRecyclerAdapter commentsAdapter;

    private String fileName, likedStatus, likesNumber, commentsNumber, subjectName, folderName, fileUrl;
    private String commentsJSON;
    private int numberOfComments, lastPos = 0;
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
            folderName = bundle.getString("folderName");
            fileName = bundle.getString("fileName");
            subjectName = bundle.getString("subjectName");

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


        new Thread(new Runnable() {
            @Override
            public void run() {


                // Remove all notifications for this post
                HelpingFunctions.deleteNotificationsForPost(MainPageT.teacher.getUsername(), subjectName, folderName, fileName, MainPageT.teacher.getUsername());

                // Back image
                backIV = findViewById(R.id.backIV);

                // Profile Image
                final ImageView profileImageIV = findViewById(R.id.profileImageIV);

                // Likes Image
                likesIV = findViewById(R.id.likesIV);

                // Liked status
                likedStatus = HelpingFunctions.getLikedStatus(MainPageT.teacher.getUsername(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName);

                // Likes number
                likesNumber = HelpingFunctions.getLikesForPost(MainPageT.teacher.getUsername(), subjectName, folderName, fileName);

                // Comment Field
                commentET = findViewById(R.id.commentET);
                sendCommentIV = findViewById(R.id.sendCommentIV);

                // Comments number
                commentsNumber = HelpingFunctions.getCommentsForPost(MainPageT.teacher.getUsername(), subjectName, folderName, fileName);
                numberOfComments = Integer.parseInt(commentsNumber);

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

                // File information
                final ArrayList<String> fileInformation = HelpingFunctions.getFileInformation(MainPageT.teacher.getUsername(), subjectName, folderName, fileName);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            commentET.requestFocus();

                            // Profile Image
                            String image = MainPageT.teacher.getProfileImageBase64();
                            if (image.equals("")) {
                                switch (MainPageT.teacher.getGender()) {
                                    case "0":
                                        profileImageIV.setImageResource(R.drawable.profile_picture_male);
                                        break;
                                    case "1":
                                        profileImageIV.setImageResource(R.drawable.profile_picture_female);
                                        break;
                                    case "2":
                                        profileImageIV.setImageResource(R.drawable.profile_picture_neutral);
                                        break;
                                }
                            } else {
                                profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(image));
                            }

                            // Liked status
                            if (likedStatus.equals("1")) {
                                likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                                likesIV.setTag("1");
                            } else {
                                likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                likesIV.setTag("0");
                            }

                            // Download button
                            fileUrl = fileInformation.get(0);
                            if (fileUrl.equals("null")) {
                                downloadTV.setVisibility(View.INVISIBLE);
                            }

                            // Title
                            fileNameTV.setText(fileName);

                            // Name
                            if (MainPageT.teacher.getFirstName().equals("") || MainPageT.teacher.getLastName().equals("") || MainPageT.teacher.getFirstName().equals("null") || MainPageT.teacher.getLastName().equals("null")) {
                                teacherNameTV.setText(R.string.message_unknown_name);
                            } else {
                                String name = MainPageT.teacher.getFirstName() + " " + MainPageT.teacher.getLastName();
                                teacherNameTV.setText(name);
                            }

                            // Username
                            teacherUsernameTV.setText(MainPageT.teacher.getUsername());

                            // Other fields

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
                displayedComments = new ArrayList<>();
                commentsJSON = HelpingFunctions.getAllCommentsJSON(MainPageT.teacher.getUsername(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName);
                getNextComments();


                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (displayedComments.size() > 0) {

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
                HelpingFunctions.sendNotificationToStudents(MainPageT.teacher.getUsername(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, "Commented on a post you commented.");
                Comment newComment = new Comment("1", MainPageT.teacher.getUsername(), MainPageT.teacher.getProfileImageBase64(), MainPageT.teacher.getGender(), commentET.getText().toString(), "0s", HelpingFunctions.getCommentTimestamp(commentET.getText().toString(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, MainPageT.teacher.getUsername()), 0, "");
                displayedComments.add(0, newComment);
                commentsAdapter.notifyItemInserted(0);
                commentET.setText("");
                numberOfComments ++;
                commentsTV.setText(String.valueOf(numberOfComments));
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

                nestedCommentsView.smoothScrollTo(0, 0);

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

        commentET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nestedScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final int scrollViewHeight = nestedScrollView.getHeight();
                        if (scrollViewHeight > 0) {
                            nestedScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            final View lastView = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                            final int lastViewBottom = lastView.getBottom() + nestedScrollView.getPaddingBottom();
                            final int deltaScrollY = lastViewBottom - scrollViewHeight - nestedScrollView.getScrollY();
                            nestedScrollView.scrollBy(0, deltaScrollY);
                        }
                    }
                });
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
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SeePostTeacher.this);
        commentsAdapter = new CommentsRecyclerAdapter(commentsRV, linearLayoutManager, SeePostTeacher.this, displayedComments);
        commentsRV.setHasFixedSize(true);
        commentsRV.setLayoutManager(linearLayoutManager);
        commentsRV.setAdapter(commentsAdapter);
        nestedCommentsView.smoothScrollTo(0, 0);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        commentsAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {

                if (displayedComments.size() < numberOfComments) {
                    displayedComments.add(null);
                    commentsAdapter.notifyItemInserted(displayedComments.size() - 1);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            displayedComments.remove(displayedComments.size() - 1);
                            commentsAdapter.notifyItemRemoved(displayedComments.size());

                            // load 10 more comments
                            getNextComments();

                            commentsAdapter.notifyDataSetChanged();
                            commentsAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });


        commentsRV.setAdapter(commentsRecyclerAdapter);
        commentsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void getNextComments() {

        int number;
        if (numberOfComments - displayedComments.size() >= 5) {
            number = 5;
        } else {
            number = numberOfComments - displayedComments.size();
        }

        displayedComments.addAll(HelpingFunctions.getCommentsFromJSON(lastPos, lastPos + number, commentsJSON));
        lastPos += number;
    }

    @Override
    public void onButtonClicked(String text, Comment comment) {

        if (!HelpingFunctions.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (text.equals("Delete comment.")) {

            commentsAdapter.deleteComment(comment);
            displayedComments.remove(comment);

            if (displayedComments.size() == 0) {
                infoTV.setVisibility(View.VISIBLE);
            }
        }
    }


    /*                                   *** A D A P T O R  ***                                   */
    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageIV;
        TextView timeTV;
        TextView usernameTV;
        TextView commentTV;
        ImageView likeIV;
        TextView likeTV;
        RelativeLayout relativeLayout;

        CommentViewHolder(View itemView) {
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


    class CommentsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_COMMENT = 0, VIEW_TYPE_LOADING = 1;
        Activity activity;
        Context context;
        LinearLayoutManager linearLayoutManager;
        private ArrayList<Comment> comments;

        LoadMore loadMore;
        boolean isLoading;
        int visibleThreshold = 5;
        int lastVisibleItem, totalItemCount;

        CommentsRecyclerAdapter(RecyclerView recyclerView, final LinearLayoutManager linearLayoutManager, Activity activity, ArrayList<Comment> comments) {
            this.activity = activity;
            this.context = activity.getApplicationContext();
            this.comments = comments;
            this.linearLayoutManager = linearLayoutManager;

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();


                    // Reached the top
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (loadMore != null) {
                            loadMore.onLoadMore();
                        }
                        isLoading = true;
                    }

                }
            });

        }

        void setLoadMore(LoadMore loadMore) {
            this.loadMore = loadMore;
        }

        @Override
        public int getItemViewType(int position) {

            if (comments.get(position) == null) {
                return VIEW_TYPE_LOADING;
            }

            return VIEW_TYPE_COMMENT;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == VIEW_TYPE_COMMENT) {
                View view = LayoutInflater.from(activity).inflate(R.layout.list_custom_comment, parent, false);
                return new CommentViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(activity).inflate(R.layout.load_more_progress_bar, parent, false);
                return new LoadingViewHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            final Comment comment = comments.get(position);

            if (holder instanceof CommentViewHolder){
                final CommentViewHolder viewHolder = (CommentViewHolder) holder;

                // Profile image
                if (comment.getImageBase64().equals("")) {
                    switch (comment.getGender()) {
                        case "0":
                            viewHolder.profileImageIV.setImageResource(R.drawable.profile_picture_male);
                            break;
                        case "1":
                            viewHolder.profileImageIV.setImageResource(R.drawable.profile_picture_female);
                            break;
                        case "2":
                            viewHolder.profileImageIV.setImageResource(R.drawable.profile_picture_neutral);
                            break;
                    }
                } else {
                    viewHolder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(comment.getImageBase64()));
                }

                viewHolder.timeTV.setText(comment.getTimeSince());
                viewHolder.usernameTV.setText(comment.getUsername());
                viewHolder.commentTV.setText(comment.getComment());
                String likes = comment.getLikes() + "";
                viewHolder.likeTV.setText(likes);

                // Like/Unlike comment
                if (comment.getLikedStatus().equals("1")) {
                    viewHolder.likeIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                    viewHolder.likeIV.setTag("1");
                } else {
                    viewHolder.likeIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    viewHolder.likeIV.setTag("0");
                }

                // LISTENERS

                // Profile image
                viewHolder.profileImageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        openProfile(comment);
                    }
                });

                // Userame
                viewHolder.usernameTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        openProfile(comment);
                    }
                });

                // Like/Unlike comment
                viewHolder.likeIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (viewHolder.likeIV.getTag().equals("0")) {
                            String result = HelpingFunctions.likeComment(comment.getComment(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, comment.getTimestamp(), MainPageT.teacher.getUsername(), comment.getUsername());
                            if (result.equals("Data inserted.")) {
                                if (!MainPageT.teacher.getUsername().equals(comment.getUsername())) {
                                    // send notification
                                    if(comment.getType().equals("0")) {
                                        // STUDENT COMMENT
                                        HelpingFunctions.sendNotificationToStudents(comment.getUsername(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, "Liked your comment.");
                                    }
                                }
                                likeUnlikeComment(comment, "1");

                            }
                        } else {
                            HelpingFunctions.unlikeComment(comment.getComment(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, comment.getTimestamp(), MainPageT.teacher.getUsername(), comment.getUsername());
                            likeUnlikeComment(comment, "0");
                        }
                    }
                });

                // Delete comment
                viewHolder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if(!HelpingFunctions.isConnected(getApplicationContext())){
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return false;
                        }


                        if (comment.getUsername().equals(MainPageT.teacher.getUsername())) {
                            // apare optiune sa sterg comentariul
                            BottomDeleteCommentDialog bottomDeleteCommentDialog = new BottomDeleteCommentDialog(comment);
                            bottomDeleteCommentDialog.show(getSupportFragmentManager(), "DELETE_COMMENT");

                            return true;
                        }
                        return true;
                    }
                });
            }

            if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        void deleteComment(Comment comment) {

            String result = HelpingFunctions.removeComment(comment.getComment(), MainPageT.teacher.getUsername(), subjectName, folderName, fileName, comment.getTimestamp(), comment.getUsername());
            if (result.equals("Success.")) {
                numberOfComments --;
                commentsTV.setText(String.valueOf(numberOfComments));
                int position = comments.indexOf(comment);

                comments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, comments.size());

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
            } else {
                Toast.makeText(context, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }

        void openProfile(Comment comment){

            if (comment.getType().equals("0")) {
                Intent intent = new Intent(context, SeeStudent.class);
                intent.putExtra("username", comment.getUsername());
                context.startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }

        }

        void likeUnlikeComment(Comment comment, String likedStatus) {
            int position = comments.indexOf(comment);


            comments.get(position).setLikedStatus(likedStatus);
            if (likedStatus.equals("1")) {
                comments.get(position).setLikes(comments.get(position).getLikes() + 1);
            } else {
                comments.get(position).setLikes(comments.get(position).getLikes() - 1);
            }

            notifyItemChanged(position);

        }



        void setLoaded() {
            isLoading = false;
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