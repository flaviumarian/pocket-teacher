package com.licence.pocketteacher.student.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Folder;
import com.licence.pocketteacher.aiding_classes.Post;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.aiding_classes.Subject;

import java.util.ArrayList;
import java.util.Collections;

public class SeeSubject extends AppCompatActivity {


    private TextView infoTV, info1TV, foldersTV, postsTV;
    private RecyclerView foldersRV, postsRV;
    private PostsAdapter postsAdapter;
    private ImageView backIV;
    private Dialog subjectDetailsPopup, domainDetailsPopup, reportPopup, reportSentPopup;

    private String usernameTeacher, subject;
    private ArrayList<Folder> folders;
    private ArrayList<Post> postsDisplayed;
    private ArrayList<String> postsJSONS;

    private Folder currentFolder = null;
    private Post currentPost = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_subject);

        getSubjectFromIntent();
        initiateComponents();

    }

    private void getSubjectFromIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            usernameTeacher = bundle.getString("teacher_username");
            subject = bundle.getString("subject");
        }
    }

    private void initiateComponents() {

        // Toolbar
        Toolbar subjectToolbar = findViewById(R.id.subjectToolbar);
        subjectToolbar.setTitle("");
        this.setSupportActionBar(subjectToolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                backIV = findViewById(R.id.backIV);
                final ImageView subjectLogoIV = findViewById(R.id.subjectLogoIV);

                // Text Views
                infoTV = findViewById(R.id.infoTV);
                foldersTV = findViewById(R.id.foldersTV);
                postsTV = findViewById(R.id.postsTV);
                info1TV = findViewById(R.id.info1TV);

                // Toolbar textview
                final TextView subjectNameTV = findViewById(R.id.subjectNameTV);

                // Folders
                folders = HelpingFunctions.getFolders(usernameTeacher, subject);
                Collections.sort(folders);

                // Get all posts JSON for each folder
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        postsJSONS = new ArrayList<>();
                        for (Folder folder : folders) {
                            postsJSONS.add(HelpingFunctions.getPostsForFolderJSON(MainPageS.student.getUsername(), usernameTeacher, subject, folder.getName()));
                        }

                    }
                }).start();


                // Recycler View
                foldersRV = findViewById(R.id.foldersRV);
                postsRV = findViewById(R.id.postsRV);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Image View
                            subjectLogoIV.setImageBitmap(HelpingFunctions.convertBase64toImage(HelpingFunctions.getSubjectImage(subject)));

                            // Toolbar textview
                            subjectNameTV.setText(subject);

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

        // List View
        if (folders.size() == 0) {
            infoTV.setVisibility(View.VISIBLE);
            foldersTV.setVisibility(View.INVISIBLE);
            postsTV.setVisibility(View.INVISIBLE);
        } else {
            infoTV.setVisibility(View.INVISIBLE);
            foldersTV.setVisibility(View.VISIBLE);
        }

        // Recycler Views
        FoldersAdapter foldersAdapter = new FoldersAdapter(folders, getApplicationContext());
        foldersRV.setAdapter(foldersAdapter);
        foldersRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        postsDisplayed = new ArrayList<>();
        postsAdapter = new PostsAdapter(postsDisplayed, getApplicationContext());
        postsRV.setAdapter(postsAdapter);
        postsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    /*                                   *** A D A P T O R S  ***                                 */
    class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder> {

        private ArrayList<Folder> folders;
        private Context context;

        FoldersAdapter(ArrayList<Folder> folders, Context context) {
            this.folders = folders;
            this.context = context;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_custom_folder_rv, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Folder folder = folders.get(position);

            holder.folderTV.setText(folder.getName());
            if (folder.isShowPosts()) {
                holder.endIV.setImageResource(R.drawable.ic_expand_less_black_24dp);
            } else {
                holder.endIV.setImageResource(R.drawable.ic_expand_more_black_24dp);
            }

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!HelpingFunctions.isConnected(context)) {
                        Toast.makeText(context, "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    showHidePosts(folder);

                    // show posts
                    showPosts(folder);
                }
            });

        }

        void showHidePosts(Folder folder) {
            int position = folders.indexOf(folder);
            if (folders.get(position).isShowPosts()) {
                folders.get(position).setShowPosts(false);
                currentFolder = null;
            } else {
                folders.get(position).setShowPosts(true);
                currentFolder = folder;

                // change all others to false as well
                for (Folder folderAux : folders) {
                    if (folderAux != folder && folderAux.isShowPosts()) {
                        folders.get(folders.indexOf(folderAux)).setShowPosts(false);
                        notifyItemChanged(folders.indexOf(folderAux));
                    }
                }
            }

            notifyItemChanged(position);
        }

        void showPosts(Folder folder) {
            postsDisplayed.clear();

            if (folder.isShowPosts()) {
                postsTV.setVisibility(View.VISIBLE);
                postsDisplayed.addAll(HelpingFunctions.getPostsFromJSON(postsJSONS.get(folders.indexOf(folder))));
                if (postsDisplayed.size() == 0) {
                    info1TV.setVisibility(View.VISIBLE);
                } else {
                    info1TV.setVisibility(View.INVISIBLE);
                }
            } else {
                postsTV.setVisibility(View.INVISIBLE);
            }

            postsAdapter.notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return folders.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView endIV;
            TextView folderTV;

            RelativeLayout relativeLayout;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                endIV = itemView.findViewById(R.id.endIV);
                folderTV = itemView.findViewById(R.id.folderTV);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
            }
        }
    }

    class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

        private ArrayList<Post> posts;
        private Context context;


        PostsAdapter(ArrayList<Post> posts, Context context) {
            this.posts = posts;
            this.context = context;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_custom_file_display_in_folder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Post post = posts.get(position);

            holder.fileTV.setText(post.getTitle());
            holder.commentsTV.setText(post.getComments());
            holder.likesTV.setText(post.getLikes());

            if (post.getLikedStatus().equals("1")) {
                holder.likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
            } else {
                holder.likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }

            holder.likesIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!HelpingFunctions.isConnected(context)) {
                        Toast.makeText(context, "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    likeUnlikePost(post);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // refresh JSON for this post
                            postsJSONS.set(position, HelpingFunctions.getPostsForFolderJSON(MainPageS.student.getUsername(), usernameTeacher, subject, currentFolder.getName()));
                        }
                    }).start();
                }
            });


            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!HelpingFunctions.isConnected(context)) {
                        Toast.makeText(context, "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentPost = post;

                    // open post
                    Intent intent = new Intent(context, SeePostStudent.class);
                    intent.putExtra("usernameTeacher", usernameTeacher);
                    intent.putExtra("folderName", currentFolder.getName());
                    intent.putExtra("subject", subject);
                    intent.putExtra("fileName", post.getTitle());
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                }
            });
        }

        void likeUnlikePost(final Post post) {

            // unlike
            if (post.getLikedStatus().equals("1")) {
                posts.get(posts.indexOf(post)).setLikedStatus("0");
                posts.get(posts.indexOf(post)).setLikes(String.valueOf(Integer.parseInt(posts.get(posts.indexOf(post)).getLikes()) - 1));
                notifyItemChanged(posts.indexOf(post));

                HelpingFunctions.unlikePost(usernameTeacher, subject, currentFolder.getName(), post.getTitle(), MainPageS.student.getUsername());
                return;
            }

            // like post
            posts.get(posts.indexOf(post)).setLikedStatus("1");
            posts.get(posts.indexOf(post)).setLikes(String.valueOf(Integer.parseInt(posts.get(posts.indexOf(post)).getLikes()) + 1));

            notifyItemChanged(posts.indexOf(post));

            String result = HelpingFunctions.likePost(usernameTeacher, subject, currentFolder.getName(), post.getTitle(), MainPageS.student.getUsername());
            if (result.equals("Data inserted.")) {

                new Thread() {
                    @Override
                    public void run() {
                        // Send notification
                        HelpingFunctions.sendNotificationToTeachers(MainPageS.student.getUsername(), usernameTeacher, subject, currentFolder.getName(), post.getTitle(), "Liked your post.");
                    }
                }.start();
            }


        }

        void updateField(){
            String likedStatus = HelpingFunctions.getLikedStatus(MainPageS.student.getUsername(), usernameTeacher, subject, currentFolder.getName(), currentPost.getTitle());
            String likes = HelpingFunctions.getLikesForPost(usernameTeacher, subject, currentFolder.getName(), currentPost.getTitle());

            posts.get(posts.indexOf(currentPost)).setLikedStatus(likedStatus);
            posts.get(posts.indexOf(currentPost)).setLikes(likes);

            notifyItemChanged(posts.indexOf(currentPost));
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView fileTV;
            ImageView likesIV;
            TextView likesTV;
            ImageView commentsIV;
            TextView commentsTV;
            ImageView endIV;


            RelativeLayout relativeLayout;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                fileTV = itemView.findViewById(R.id.fileTV);
                likesIV = itemView.findViewById(R.id.likesIV);
                likesTV = itemView.findViewById(R.id.likesTV);
                commentsIV = itemView.findViewById(R.id.commentsIV);
                commentsTV = itemView.findViewById(R.id.commentsTV);
                endIV = itemView.findViewById(R.id.endIV);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
            }
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
                if (!HelpingFunctions.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

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

                        if (!HelpingFunctions.isConnected(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

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

                if (!HelpingFunctions.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

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

                        if (!HelpingFunctions.isConnected(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

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

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            postsAdapter.updateField();
            currentPost = null;

        }
    }

    @Override
    public void onBackPressed() {

        if (!HelpingFunctions.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }


        finish();
        SeeSubject.this.overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}