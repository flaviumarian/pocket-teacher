package com.licence.pocketteacher.student.profile.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.student.search.SeePostStudent;
import com.licence.pocketteacher.student.search.SeeTeacher;
import com.licence.pocketteacher.aiding_classes.Notification;

import java.util.ArrayList;

public class SeeNotifications extends AppCompatActivity {

    private ImageView backIV;
    private TextView infoTV;
    private RecyclerView notificationsRV;
    private NotificationsRecyclerAdapter notificationsRecyclerAdapter;
    private ArrayList<Notification> notifications;

    boolean firstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_notifications);

        initiateComponents();

    }


    private void initiateComponents() {

        // Toolbar
        Toolbar subjectToolbar = findViewById(R.id.notificationsToolbar);
        subjectToolbar.setTitle("");
        this.setSupportActionBar(subjectToolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Image View
                backIV = findViewById(R.id.backIV);

                // Text View
                infoTV = findViewById(R.id.infoTV);

                // Recycler View
                notificationsRV = findViewById(R.id.notificationsRV);

                // Array List
                notifications = HelpingFunctions.getAllNotifications(MainPageS.student.getUsername());

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (notifications.size() == 0) {
                                infoTV.setVisibility(View.VISIBLE);
                            } else {

                                infoTV.setVisibility(View.INVISIBLE);
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

    private void setListeners() {

        // Image View
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Recycler View
        notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(notifications, SeeNotifications.this);
        notificationsRV.setAdapter(notificationsRecyclerAdapter);
        notificationsRV.setLayoutManager(new LinearLayoutManager(SeeNotifications.this));

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private void reloadNotifications(){

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


        new Thread(new Runnable() {
            @Override
            public void run() {
                notifications.clear();
                notifications.addAll(HelpingFunctions.getAllNotifications(MainPageS.student.getUsername()));

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            notificationsRecyclerAdapter.notifyDataSetChanged();
                            if(notifications.size() > 0){
                                infoTV.setVisibility(View.INVISIBLE);
                            }else{
                                infoTV.setVisibility(View.VISIBLE);
                            }

                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }


    /*                                   *** A D A P T O R  ***                                   */
    class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.ViewHolder> {

        private ArrayList<Notification> notifications;
        private Context context;

        public NotificationsRecyclerAdapter(ArrayList<Notification> notifications, Context context) {
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
            if (notification.getProfileImageBase64().equals("")) {
                switch (notification.getGender()) {
                    case "0":
                        holder.profileImageIV.setImageResource(R.drawable.profile_picture_male);
                        break;
                    case "1":
                        holder.profileImageIV.setImageResource(R.drawable.profile_picture_female);
                        break;
                    case "2":
                        holder.profileImageIV.setImageResource(R.drawable.profile_picture_neutral);
                        break;
                }
            } else {
                holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(notification.getProfileImageBase64()));
            }


            holder.timeTV.setText(notification.getTime());
            holder.usernameTV.setText(notification.getUsername());
            holder.messageTV.setText(notification.getMessage());

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!HelpingFunctions.isConnected(context)) {
                        Toast.makeText(context, "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (holder.messageTV.getText().toString().equals("approved your follow request.")) {
                        HelpingFunctions.deleteNotificationApprovedFollowRequest(notification.getUsername(), MainPageS.student.getUsername());

                        Intent intent = new Intent(context, SeeTeacher.class);
                        intent.putExtra("username", notification.getUsername());
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                    } else {

                        Intent intent = new Intent(context, SeePostStudent.class);
                        intent.putExtra("usernameTeacher", notification.getUsernamePoster());
                        intent.putExtra("folderName", notification.getFolderName());
                        intent.putExtra("subject", notification.getSubjectName());
                        intent.putExtra("fileName", notification.getPostName());

                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);

                    }

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return notifications.size();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clear_notifications, menu);

        // Setting up menu options
        MenuItem item = menu.getItem(0).getSubMenu().getItem(0);
        SpannableString s = new SpannableString("Clear notifications");
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.clearNotifications:

                if (!HelpingFunctions.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                    break;
                }

                notifications.clear();
                notificationsRecyclerAdapter.notifyDataSetChanged();
                infoTV.setVisibility(View.VISIBLE);
                notificationsRV.setVisibility(View.INVISIBLE);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);

                HelpingFunctions.deleteAllNotifications(MainPageS.student.getUsername());
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(firstLaunch){
            firstLaunch = false;
            return;
        }

        reloadNotifications();
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