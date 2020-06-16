package com.licence.pocketteacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.aiding_classes.Conversation;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.teacher.MainPageT;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;

public class MessageConversations extends AppCompatActivity {

    private int accountType;
    private RecyclerView conversationsRV;
    private ConversationsAdapter conversationsAdapter;
    private ArrayList<Conversation> conversations;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_conversations);

        getIntentType();
        initiateComponents();
    }

    private void getIntentType(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            accountType = bundle.getInt("type");
        }
    }

    private void initiateComponents(){

        // Image View
        ImageView backIV = findViewById(R.id.backIV);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final ProgressDialog loading = ProgressDialog.show(MessageConversations.this, "Please wait", "Loading...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(accountType == 0){
                    conversations = HelpingFunctions.getAllConversations(MainPageS.student.getUsername());
                }else{
                    conversations = HelpingFunctions.getAllConversations(MainPageT.teacher.getUsername());
                }

                Collections.sort(conversations, Collections.<Conversation>reverseOrder());



                // Recycler View
                conversationsRV = findViewById(R.id.conversationsRV);
                conversationsAdapter = new ConversationsAdapter(conversations, MessageConversations.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conversationsRV.setAdapter(conversationsAdapter);
                        conversationsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        loading.dismiss();
                    }
                });
            }
        }).start();

    }

    class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{

        private ArrayList<Conversation> conversations;
        private Context context;

        public ConversationsAdapter(ArrayList<Conversation> conversations, Context context){
            this.conversations = conversations;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_conversation, parent, false);
            ViewHolder holder = new ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final Conversation conversation = conversations.get(position);

            // Profile image
            if(conversation.getImageBase64().equals("")){
                switch(conversation.getGender()){
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
                holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(conversation.getImageBase64()));
            }

            holder.usernameTV.setText(conversation.getUsername());
            holder.lastMessageTV.setText(conversation.getLastMessage());
            holder.timeTV.setText(conversation.getTime());

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessagingPage.class);
                    if(accountType == 0){
                        intent.putExtra("username_sender", MainPageS.student.getUsername());
                    }else{
                        intent.putExtra("username_sender", MainPageT.teacher.getUsername());
                    }
                    intent.putExtra("type", accountType);
                    intent.putExtra("username_receiver", holder.usernameTV.getText().toString());
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                }
            });
        }

        public void updateConversation(String username){
            int position = -1;

            for(int i=0;i<conversations.size(); i++){
                if(conversations.get(i).getUsername().equals(username)){
                    position = i;
                    break;
                }
            }

            if(position == -1){
                return;
            }

            String usernameSender;
            if(accountType == 0){
                usernameSender = MainPageS.student.getUsername();
            }else{
                usernameSender = MainPageT.teacher.getUsername();
            }

            ArrayList<String> newData = HelpingFunctions.getLastMessage(usernameSender, username);
            conversations.get(position).setLastMessage(newData.get(0));
            conversations.get(position).setTime(newData.get(1));

            if(position != 0){
                Collections.swap(conversations, position, 0);
                notifyItemChanged(0);
            }

            notifyItemChanged(position);


        }

        @Override
        public int getItemCount() {
            return conversations.size();
        }



        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView profileImageIV;
            TextView usernameTV;
            TextView lastMessageTV;
            TextView timeTV;
            RelativeLayout relativeLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                profileImageIV = itemView.findViewById(R.id.profileImageIV);
                usernameTV = itemView.findViewById(R.id.usernameTV);
                lastMessageTV = itemView.findViewById(R.id.lastMessageTV);
                timeTV = itemView.findViewById(R.id.timeTV);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK) {
                String username = data.getStringExtra("username");
                conversationsAdapter.updateConversation(username);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
