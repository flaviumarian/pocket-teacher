package com.licence.pocketteacher.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.Conversation;
import com.licence.pocketteacher.aiding_classes.TextMessage;
import com.licence.pocketteacher.messaging.new_conversation_student_account.StartConversationListStudent;
import com.licence.pocketteacher.messaging.new_conversation_teacher_account.StartConversationListTeacher;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.teacher.MainPageT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class MessageConversations extends AppCompatActivity {

    private int accountType, messagesCount, conversationsBlocked = 0;
    private RecyclerView conversationsRV;
    private ConversationsAdapter conversationsAdapter;
    private ArrayList<Conversation> conversations;
    private String masterUsername;
    private boolean keepChecking = true, skipDelay = false, isDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_conversations);

        getIntentType();
        initiateComponents();

    }

    private void getIntentType() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            accountType = bundle.getInt("type");
            if (accountType == 0) {
                masterUsername = MainPageS.student.getUsername();
            } else {
                masterUsername = MainPageT.teacher.getUsername();
            }
        }
    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Image View
                final ImageView backIV = findViewById(R.id.backIV);
                final ImageView startNewConversationIV = findViewById(R.id.startNewConversationIV);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });

                            startNewConversationIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!HelpingFunctions.isConnected(getApplicationContext())) {
                                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    startNewConversation();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Get all new conversations and count of messages
                conversations = HelpingFunctions.getAllConversations(masterUsername);
                messagesCount = HelpingFunctions.getNumberOfAllMessages(masterUsername);

                if (conversations.size() == 0) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.infoTV).setVisibility(View.VISIBLE);
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }


                // Order in terms of time of last message
                Collections.sort(conversations, Collections.<Conversation>reverseOrder());
                if (accountType == 1) {
                    // Teacher account - no need to see the blocked messages
                    removeBlockedConversations();
                }


                // Recycler View
                conversationsRV = findViewById(R.id.conversationsRV);
                conversationsAdapter = new ConversationsAdapter(conversations, MessageConversations.this);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            conversationsRV.setAdapter(conversationsAdapter);
                            conversationsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void checkForConversationChanges() {

        final Context context = getApplicationContext();

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {


                    // Wait for everything to be settled in
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Continuously check for new updates every 1.5s
                    while (keepChecking) {

                        if(!isDisplayed){
                            return;
                        }


                        if (!skipDelay) {
                            try {
                                Thread.sleep(1500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            skipDelay = false;
                        }

                        if (!HelpingFunctions.isConnected(context)) {
                            continue;
                        }

                        final int numberOfMessages = HelpingFunctions.getNumberOfAllMessages(masterUsername);
                        try {
                            if (numberOfMessages != messagesCount) {

                                if (!HelpingFunctions.isConnected(context)) {
                                    continue;
                                }
                                // check whether a new person messaged or one of the existing needs updating
                                boolean hasMoreOrLess = false;
                                if ((conversations.size() + conversationsBlocked) != HelpingFunctions.getNumberOfConversations(masterUsername)) {
                                    hasMoreOrLess = true;
                                }

                                if (!hasMoreOrLess) {
                                    // ONE OF THE EXISTING CONVERSATIONS IS UPDATED
                                    if (!HelpingFunctions.isConnected(context)) {
                                        continue;
                                    }
                                    final CountDownLatch latch = new CountDownLatch(1);
                                    for (int i = 0; i < conversations.size(); i++) {

                                        int newNumberOfMessages = HelpingFunctions.getNumberOfMessages(masterUsername, conversations.get(i).getUsername());
                                        if (conversations.get(i).getNumberOfMessages() != newNumberOfMessages) {
                                            conversations.get(i).setNumberOfMessages(newNumberOfMessages);
                                            final int position = i;

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    try {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                conversationsAdapter.updateConversation(conversations.get(position).getUsername());
                                                                latch.countDown();
                                                            }
                                                        });

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();

                                            try {
                                                latch.await();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            i = -1;

                                        }
                                    }


                                } else {
                                    if (!HelpingFunctions.isConnected(context)) {
                                        continue;
                                    }
                                    // NUMBER OF CONVERSATIONS IS DIFFERENT
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            final ArrayList<Conversation> newConversations = HelpingFunctions.getAllConversations(masterUsername);
                                            try {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        ArrayList<Integer> positionsToDelete = new ArrayList<>();

                                                        // Any deleted accounts
                                                        for (Conversation conv : conversations) {
                                                            if (conversationIsDeleted(newConversations, conv.getUsername())) {
                                                                positionsToDelete.add(conversations.indexOf(conv));
                                                            }
                                                        }

                                                        for (int i = positionsToDelete.size() - 1; i >= 0; i--) {
                                                            conversationsAdapter.removeConversation(conversations.get(i).getUsername());
                                                        }

                                                        // Any new conversations
                                                        for (Conversation conv : newConversations) {
                                                            if (conversationAlreadyDisplayed(conv)) {
                                                                continue;
                                                            }
                                                            conversationsAdapter.addConversation(conv);
                                                        }
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        } catch (Exception e) {
                            return;
                        }

                        messagesCount = numberOfMessages;
                    }
                }

            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean conversationAlreadyDisplayed(Conversation conv) {
        for (Conversation c : conversations) {
            if (c.getUsername().equals(conv.getUsername())) {
                return true;
            }
        }

        return false;
    }

    private boolean conversationIsDeleted(ArrayList<Conversation> newConversations, String username) {
        for (Conversation conv : newConversations) {
            if (conv.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    private void removeBlockedConversations() {

        ArrayList<Integer> positionsToRemove = new ArrayList<>();
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getBlocked() == 1) {
                positionsToRemove.add(i);
                conversationsBlocked++;
            }
        }

        for (int i = positionsToRemove.size() - 1; i >= 0; i--) {
            int index = positionsToRemove.get(i);
            conversations.remove(index);
        }
    }

    private void startNewConversation() {

        keepChecking = false;

        if (accountType == 0) {
            Intent intent = new Intent(this, StartConversationListStudent.class);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
        } else {
            Intent intent = new Intent(this, StartConversationListTeacher.class);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
        }
    }

    class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

        private ArrayList<Conversation> conversations;
        private Context context;

        public ConversationsAdapter(ArrayList<Conversation> conversations, Context context) {
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
            if (conversation.getImageBase64().equals("")) {
                switch (conversation.getGender()) {
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
                holder.profileImageIV.setImageBitmap(HelpingFunctions.convertBase64toImage(conversation.getImageBase64()));
            }


            holder.usernameTV.setText(conversation.getUsername());
            holder.lastMessageTV.setText(conversation.getLastMessage());
            if(conversation.getSeenStatus() == 0){
                holder.lastMessageTV.setTypeface(holder.lastMessageTV.getTypeface(), Typeface.BOLD);
            }
            holder.timeTV.setText(conversation.getTimeSince());

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!HelpingFunctions.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    keepChecking = false;

                    Intent intent = new Intent(context, MessagingPage.class);
                    intent.putExtra("username_sender", masterUsername);
                    intent.putExtra("type", accountType);
                    intent.putExtra("username_receiver", conversation.getUsername());
                    intent.putExtra("blocked", conversation.getBlocked());
                    intent.putExtra("image", conversation.getImageBase64());
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
                }
            });
        }

        public void updateConversation(String username) {
            int position = -1;


            for (int i = 0; i < conversations.size(); i++) {
                if (conversations.get(i).getUsername().equals(username)) {
                    position = i;
                    break;
                }
            }

            if (position == -1) {
                return;
            }

            TextMessage lastTextMessage = HelpingFunctions.getLastMessage(masterUsername, username);

            if(conversations.get(position).getTime().equals(lastTextMessage.getFullDate())){
                conversations.get(position).setSeenStatus(lastTextMessage.getSeenStatus());
                notifyItemChanged(position);
                return;
            }

            conversations.get(position).setLastMessage(lastTextMessage.getMessage());
            conversations.get(position).setTime(lastTextMessage.getFullDate());
            conversations.get(position).setSeenStatus(lastTextMessage.getSeenStatus());



            if (position != 0) {
                Conversation convToMove = conversations.get(position);
                conversations.remove(position);
                conversations.add(0, convToMove);
                notifyDataSetChanged();
            }

            notifyItemChanged(position);

        }

        public void addConversation(Conversation conversation) {

            conversations.add(0, conversation);
            notifyItemInserted(0);
        }

        public void removeConversation(String username) {
            int position = -1;
            for (Conversation conv : conversations) {
                if (conv.getUsername().equals(username)) {
                    position = conversations.indexOf(conv);
                }
            }

            if (position == -1) {
                return;
            }

            conversations.remove(position);
            notifyItemRemoved(position);
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


        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String username = data.getStringExtra("username");
                conversationsAdapter.updateConversation(username);
            }
        } else if (requestCode == 1) {
            skipDelay = true;
        }

        keepChecking = true;
        checkForConversationChanges();
    }

    @Override
    public void onBackPressed() {

        if (!HelpingFunctions.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();

        keepChecking = false;


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // When minimizing the app - to stop the thread for looking for internet connection
        isDisplayed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // When maximizing the app - to start the thread for looking for internet connection
        isDisplayed = true;
        checkForConversationChanges();
    }

}
