package com.licence.pocketteacher.student.home;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Post;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.student.search.SeePostStudent;
import com.licence.pocketteacher.student.search.SeeTeacher;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FragmentHomeS extends Fragment {

    private View view;
    private RecyclerView postsRV;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int lastPos = 0;
    private int positionPostOpened;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_s, container, false);

        initiateComponents();
        positionPostOpened = -1; // for when returning to the activity

        return view;
    }


    private void initiateComponents() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                // Text View
                final TextView infoTV = view.findViewById(R.id.infoTV);

                // Image View
                final ImageView arrowIV = view.findViewById(R.id.arrowIV);

                // Swipe Refresh Layout
                swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

                // ArrayList
                posts = new ArrayList<>();

                getNextPosts();

                // Recycler View
                postsRV = view.findViewById(R.id.postsRV);

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (MainPageS.allPosts.size() == 0) {
                                if (Integer.parseInt(HelpingFunctions.getFollowing(MainPageS.student.getUsername())) > 0) {
                                    infoTV.setText(R.string.message_home_2);
                                }
                                infoTV.setVisibility(View.VISIBLE);
                                arrowIV.setVisibility(View.VISIBLE);

                                swipeRefreshLayout.setRefreshing(false);
                                swipeRefreshLayout.setEnabled(false);

                            } else {
                                infoTV.setVisibility(View.INVISIBLE);
                                arrowIV.setVisibility(View.INVISIBLE);
                                swipeRefreshLayout.setEnabled(true);
                            }

                            // Recycler View
                            postsRV.setVisibility(View.VISIBLE);
                            postsRV.setHasFixedSize(true); // improves performance
                            postsRV.setLayoutManager(new LinearLayoutManager(view.getContext()));

                            // Adapter
                            postAdapter = new PostAdapter(postsRV, getActivity(), posts);
                            postsRV.setAdapter(postAdapter);

                            if (!MainPageS.needsRefresh) {
                                // Load more
                                postAdapter.setLoadMore(new LoadMore() {
                                    @Override
                                    public void onLoadMore() {

                                        if (posts.size() < MainPageS.allPosts.size()) {
                                            posts.add(null);
                                            postAdapter.notifyItemInserted(posts.size() - 1);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    posts.remove(posts.size() - 1);
                                                    postAdapter.notifyItemRemoved(posts.size());

                                                    // load 5 more posts
                                                    getNextPosts();

                                                    postAdapter.notifyDataSetChanged();
                                                    postAdapter.setLoaded();
                                                }
                                            }, 1000);
                                        } else {
                                            posts.add(new Post(true));
                                            postAdapter.notifyItemInserted(posts.size() - 1);
                                        }
                                    }
                                });
                            }

                            MainPageS.resetBadge();

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

        // Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                postsRV.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainPageS.allPosts.clear();
                        postAdapter.notifyDataSetChanged();
                        MainPageS.generateAllPosts();
                        lastPos = 0;
                        initiateComponents();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void getNextPosts() {

        int number;
        if (MainPageS.allPosts.size() - posts.size() >= 5) {
            number = 5;
        } else {
            number = MainPageS.allPosts.size() - posts.size();
        }

        for (int i = lastPos; i < lastPos + number; i++) {
            posts.add(MainPageS.allPosts.get(i));
        }
        lastPos += number;
    }


    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageIV;
        TextView teacherNameTV;
        TextView teacherUsernameTV;
        TextView timeTV;
        TextView titleTV;
        TextView subjectTV;
        TextView folderTV;
        TextView descriptionTV;
        ImageView likesIV;
        TextView likesTV;
        TextView commentsTV;
        TextView downloadTV;
        RelativeLayout relativeLayout; // post as a whole

        PostViewHolder(View itemView) {
            super(itemView);

            profileImageIV = itemView.findViewById(R.id.profileImageIV);
            teacherNameTV = itemView.findViewById(R.id.teacherNameTV);
            teacherUsernameTV = itemView.findViewById(R.id.teacherUsernameTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            subjectTV = itemView.findViewById(R.id.subjectTV);
            folderTV = itemView.findViewById(R.id.folderTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            likesIV = itemView.findViewById(R.id.likesIV);
            likesTV = itemView.findViewById(R.id.likesTV);
            commentsTV = itemView.findViewById(R.id.commentsTV);
            downloadTV = itemView.findViewById(R.id.downloadTV);

            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }

    class EndingViewHolder extends RecyclerView.ViewHolder {

        EndingViewHolder(View itemView) {
            super(itemView);
        }
    }

    class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1, VIEW_TYPE_ENDING = 2;
        LoadMore loadMore;
        boolean isLoading;
        Activity activity;
        Context context;
        ArrayList<Post> posts;
        int visibleThreshold = 5;
        int lastVisibleItem, totalItemCount;


        PostAdapter(RecyclerView recyclerView, Activity activity, ArrayList<Post> posts) {
            this.activity = activity;
            this.context = activity.getApplicationContext();
            this.posts = posts;


            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);


                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

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

            if (posts.get(position) != null && posts.get(position).isLastPost()) {
                return VIEW_TYPE_ENDING;
            }

            return posts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(activity).inflate(R.layout.list_custom_post, parent, false);

                return new PostViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(activity).inflate(R.layout.load_more_progress_bar, parent, false);

                return new LoadingViewHolder(view);
            } else if (viewType == VIEW_TYPE_ENDING) {
                View view = LayoutInflater.from(activity).inflate(R.layout.end_of_scrolling, parent, false);

                return new EndingViewHolder(view);
            }


            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof PostViewHolder) {

                final Post post = posts.get(position);
                final PostViewHolder viewHolder = (PostViewHolder) holder;

                // Profile image
                if (post.getProfileImageBase64().equals("")) {
                    switch (post.getGender()) {
                        case "0":
                            viewHolder.profileImageIV.setImageResource(R.drawable.profile_picture_male);
                            break;
                        case "1":
                            viewHolder.profileImageIV.setImageResource(R.drawable.profile_picture_female);
                            break;
                        case "2":
                            viewHolder.profileImageIV.setImageResource(0);
                            break;
                    }
                } else {
                    viewHolder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(post.getProfileImageBase64()));
                }

                // Name
                viewHolder.teacherNameTV.setText(post.getName());

                // Username
                viewHolder.teacherUsernameTV.setText(post.getUsername());

                // Time
                viewHolder.timeTV.setText(post.getTimeSince());

                // Title
                viewHolder.titleTV.setText(post.getTitle());

                // Subject
                viewHolder.subjectTV.setText(post.getSubject());

                // Folder
                viewHolder.folderTV.setText(post.getFolder());

                // Description
                viewHolder.descriptionTV.setText(post.getDescription());

                // Likes image
                if (post.getLikedStatus().equals("1")) {
                    viewHolder.likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                    viewHolder.likesIV.setTag("1");
                } else {
                    viewHolder.likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    viewHolder.likesIV.setTag("0");
                }

                // Likes number
                viewHolder.likesTV.setText(post.getLikes());

                // Comments number
                viewHolder.commentsTV.setText(post.getComments());

                // Download button
                if (post.getFileUrl().equals("null")) {
                    viewHolder.downloadTV.setVisibility(View.INVISIBLE);
                }

                // LISTENERS

                // Profile image
                viewHolder.profileImageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SeeTeacher.class);
                        intent.putExtra("username", post.getUsername());
                        v.getContext().startActivity(intent);
                        activity.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                });

                // Name
                viewHolder.teacherNameTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SeeTeacher.class);
                        intent.putExtra("username", post.getUsername());
                        v.getContext().startActivity(intent);
                        activity.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                });

                // username
                viewHolder.teacherUsernameTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SeeTeacher.class);
                        intent.putExtra("username", post.getUsername());
                        v.getContext().startActivity(intent);
                        activity.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    }
                });


                // Like/unlike post
                viewHolder.likesIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.likesIV.getTag().equals("0")) {

                            String result = HelpingFunctions.likePost(viewHolder.teacherUsernameTV.getText().toString(), viewHolder.subjectTV.getText().toString(), viewHolder.folderTV.getText().toString(), viewHolder.titleTV.getText().toString(), MainPageS.student.getUsername());
                            if (result.equals("Data inserted.")) {

                                new Thread() {
                                    @Override
                                    public void run() {
                                        // Send notification
                                        HelpingFunctions.sendNotification(viewHolder.teacherUsernameTV.getText().toString(), MainPageS.student.getUsername() + " liked your post.");
                                    }
                                }.start();
                            }
                            viewHolder.likesIV.setImageResource(R.drawable.ic_favorite_red_24dp);
                            viewHolder.likesIV.setTag("1");

                            int numLikes = Integer.parseInt(viewHolder.likesTV.getText().toString()) + 1;
                            String numLikesString = numLikes + "";
                            viewHolder.likesTV.setText(numLikesString);
                            posts.get(position).setLikes(numLikesString);
                            posts.get(position).setLikedStatus("1");

                        } else {

                            HelpingFunctions.unlikePost(viewHolder.teacherUsernameTV.getText().toString(), viewHolder.subjectTV.getText().toString(), viewHolder.folderTV.getText().toString(), viewHolder.titleTV.getText().toString(), MainPageS.student.getUsername());
                            viewHolder.likesIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            viewHolder.likesIV.setTag("0");

                            int numLikes = Integer.parseInt(viewHolder.likesTV.getText().toString()) - 1;
                            String numLikesString = numLikes + "";
                            viewHolder.likesTV.setText(numLikesString);
                            posts.get(position).setLikes(numLikesString);
                            posts.get(position).setLikedStatus("0");


                        }
                    }
                });


                viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog loading = ProgressDialog.show(view.getContext(), "Please wait", "Loading...", true);
                        new Thread() {
                            @Override
                            public void run() {

                                positionPostOpened = position;

                                Intent intent = new Intent(view.getContext(), SeePostStudent.class);
                                intent.putExtra("usernameTeacher", post.getUsername());
                                intent.putExtra("folderName", post.getFolder());
                                intent.putExtra("subject", post.getSubject());
                                intent.putExtra("fileName", post.getTitle());
                                intent.putExtra("likedStatus", post.getLikedStatus());
                                intent.putExtra("likes", post.getLikes());
                                intent.putExtra("comments", post.getComments());
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);

                                try {
                                    getActivity().runOnUiThread(new Runnable() {
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

                // Download button
                if (!post.getFileUrl().equals("null")) {
                    viewHolder.downloadTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = Uri.parse(post.getFileUrl());
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                Long reference = downloadManager.enqueue(request);

                                Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Snackbar.make(view, "An error occurred. Please try again later.", Snackbar.LENGTH_SHORT);
                            }
                        }
                    });
                }

            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        void setLoaded() {
            isLoading = false;
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MainPageS.needsRefresh) {

                                swipeRefreshLayout.setRefreshing(true);
                                swipeRefreshLayout.setEnabled(true);
                                postsRV = view.findViewById(R.id.postsRV);
                                postsRV.setVisibility(View.INVISIBLE);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainPageS.allPosts.clear();
                                        MainPageS.generateAllPosts();
                                        lastPos = 0;
                                        initiateComponents();
                                        postAdapter.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);

                                    }
                                }, 2000);


                                MainPageS.needsRefresh = false;

                            } else {
                                if (positionPostOpened != -1) {
                                    Post post = posts.get(positionPostOpened);
                                    posts.get(positionPostOpened).setLikedStatus(HelpingFunctions.getLikedStatus(MainPageS.student.getUsername(), post.getUsername(), post.getSubject(), post.getFolder(), post.getTitle()));
                                    posts.get(positionPostOpened).setLikes(HelpingFunctions.getLikesForPost(post.getUsername(), post.getSubject(), post.getFolder(), post.getTitle()));
                                    posts.get(positionPostOpened).setComments(HelpingFunctions.getCommentsForPost(post.getUsername(), post.getSubject(), post.getFolder(), post.getTitle()));
                                    postAdapter.notifyItemChanged(positionPostOpened);

                                    positionPostOpened = -1;
                                }
                            }


                            // Notification Badge
                            MainPageS.resetBadge();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
