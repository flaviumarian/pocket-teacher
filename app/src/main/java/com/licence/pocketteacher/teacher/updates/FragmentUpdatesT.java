package com.licence.pocketteacher.teacher.updates;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Notification;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.folder.subject.files.SeePostTeacher;
import com.licence.pocketteacher.teacher.updates.follow_requests.SeeFollowRequests;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentUpdatesT extends Fragment {

    private View view;
    private CardView followRequestsC;
    private TextView followRequestsTV, infoTV;
    private RecyclerView notificationsRV;
    private NotificationsRecyclerAdapter notificationsRecyclerAdapter;
    private ArrayList<Notification> notifications;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_updates_t, container, false);

        setHasOptionsMenu(true);
        initiateComponents();

        // Delete all notifications
        NotificationManager manager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        return view;
    }

    private void initiateComponents(){

        // Toolbar
        Toolbar profileToolbar = view.findViewById(R.id.updatesToolbar);
        profileToolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(profileToolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Text View
                infoTV = view.findViewById(R.id.infoTV);

                // Follow Request
                final String numberFollowerRequests = HelpingFunctions.getNumberOfFollowRequests(MainPageT.teacher.getUsername());
                followRequestsC = view.findViewById(R.id.followRequestsC);
                followRequestsTV = view.findViewById(R.id.followRequestsTV);

                // Recycler View
                notificationsRV = view.findViewById(R.id.notificationsRV);

                // Array List
                notifications = HelpingFunctions.getAllNotifications(MainPageT.teacher.getUsername());

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (numberFollowerRequests.equals("0") || numberFollowerRequests.equals("None")) {
                                followRequestsC.setVisibility(View.INVISIBLE);
                            } else {
                                followRequestsC.setVisibility(View.VISIBLE);
                                followRequestsTV.setText(numberFollowerRequests);
                            }

                            if (notifications.size() == 0) {
                                infoTV.setVisibility(View.VISIBLE);
                            } else {
                                infoTV.setVisibility(View.INVISIBLE);
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

    private void setListeners(){

        // Card View
        followRequestsC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), SeeFollowRequests.class);
                startActivityForResult(intent, 0);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        });


        // Recycler View
        notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(notifications, view.getContext());
        notificationsRV.setAdapter(notificationsRecyclerAdapter);
        notificationsRV.setLayoutManager(new LinearLayoutManager(view.getContext()));

        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }



    /*                                   *** A D A P T O R  ***                                   */
    class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.ViewHolder>{

        private ArrayList<Notification> notifications;
        private Context context;

        public NotificationsRecyclerAdapter(ArrayList<Notification> notifications, Context context){
            this.notifications = notifications;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_custom_notification, parent, false);
            ViewHolder holder = new ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final Notification notification = notifications.get(position);

            // Profile image
            if(notification.getProfileImageBase64().equals("")){
                switch(notification.getGender()){
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
                holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(notification.getProfileImageBase64()));
            }

            holder.timeTV.setText(notification.getTime());
            holder.usernameTV.setText(notification.getUsername());
            holder.messageTV.setText(notification.getMessage());

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ProgressDialog loading = ProgressDialog.show(context, "Please wait", "Loading...", true);
                    new Thread() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(context, SeePostTeacher.class);
                            intent.putExtra("fileName", notification.getPostName());
                            intent.putExtra("likedStatus", HelpingFunctions.getLikedStatus(MainPageT.teacher.getUsername(), MainPageT.teacher.getUsername(), notification.getSubjectName(), notification.getFolderName(), notification.getPostName()));
                            intent.putExtra("likes", HelpingFunctions.getLikesForPost(MainPageT.teacher.getUsername(), notification.getSubjectName(), notification.getFolderName(), notification.getPostName()));
                            intent.putExtra("comments", HelpingFunctions.getCommentsForPost(MainPageT.teacher.getUsername(), notification.getSubjectName(), notification.getFolderName(), notification.getPostName()));
                            intent.putExtra("fromNotifications", true);
                            intent.putExtra("subjectName", notification.getSubjectName());
                            intent.putExtra("folderName", notification.getFolderName());
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

                    delete(position);
                    HelpingFunctions.deleteNotification(MainPageT.teacher.getUsername(), notification.getSubjectName(), notification.getFolderName(), notification.getPostName(), notification.getMessage(), notification.getUsername(), MainPageT.teacher.getUsername(), notification.getComment());
                }
            });
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }


        private void delete(int position){
            notifications.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notifications.size());
            MainPageT.badgeDrawable.setNumber(notifications.size());
            if(notifications.size() == 0 ){
                infoTV.setVisibility(View.VISIBLE);
                MainPageT.badgeDrawable.setVisible(false);
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView profileImageIV;
            TextView timeTV;
            TextView usernameTV;
            TextView messageTV;
            RelativeLayout relativeLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                profileImageIV = itemView.findViewById(R.id.profileImageIV);
                timeTV = itemView.findViewById(R.id.timeTV);
                usernameTV = itemView.findViewById(R.id.usernameTV);
                messageTV = itemView.findViewById(R.id.messageTV);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
            }
        }
    }


    /*                      *** T O O L B A R    M E N U ***                         */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_clear_notifications, menu);

        // Setting up menu options
        MenuItem item = menu.getItem(0).getSubMenu().getItem(0);
        SpannableString s = new SpannableString("Clear notifications");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.clearNotifications:
                notifications.clear();
                notificationsRecyclerAdapter.notifyDataSetChanged();
                infoTV.setVisibility(View.VISIBLE);
                MainPageT.badgeDrawable.setVisible(false);

                HelpingFunctions.deleteAllNotifications(MainPageT.teacher.getUsername());
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            // from follow requests
            String numberFollowerRequests = HelpingFunctions.getNumberOfFollowRequests(MainPageT.teacher.getUsername());
            if(numberFollowerRequests.equals("0") || numberFollowerRequests.equals("None")){
                followRequestsC.setVisibility(View.INVISIBLE);
            } else{
                followRequestsTV.setText(numberFollowerRequests);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Notification Badge
        MainPageT.resetBadge();
    }
}
