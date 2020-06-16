package com.licence.pocketteacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.aiding_classes.TextMessage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

import java.util.ArrayList;

public class MessagingPage extends AppCompatActivity {

    private EditText messageET;
    private RecyclerView messagesRV;
    private ArrayList<TextMessage> messages;
    private MessageAdapter messageAdapter;

    private String usernameSender, usernameReceiver;
    private int accountType;
    private int messagesCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_page);

        getIntentData();
        initiateComponents();
        checkForNewMessages();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            usernameSender = bundle.getString("username_sender");
            accountType = bundle.getInt("type");
            usernameReceiver = bundle.getString("username_receiver");
        }
    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                final TextView usernameTV = findViewById(R.id.usernameTV);
                messageET = findViewById(R.id.messageET);

                // ImageView
                ImageView backIV = findViewById(R.id.backIV);
                backIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                final ImageView sendMessageIV = findViewById(R.id.sendMessageIV);
                sendMessageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (HelpingFunctions.isEditTextEmpty(messageET)) {
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HelpingFunctions.sendMessage(usernameSender, usernameReceiver, messageET.getText().toString());

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("username", usernameReceiver);
                                setResult(Activity.RESULT_OK,  returnIntent);

                            }
                        }).start();

                        messages.add(new TextMessage(usernameSender, messageET.getText().toString(), accountType));
                        messagesCount++;
                        messageET.setText("");
                        messagesRV.smoothScrollToPosition(messagesRV.getAdapter().getItemCount());

                        messageAdapter.notifyDataSetChanged();
                    }
                });

                messageET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (messageET.getText().length() > 0) {
                            sendMessageIV.setImageResource(R.drawable.logo_send);
                        } else {
                            sendMessageIV.setImageResource(R.drawable.logo_send_grey);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                messageET.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(200);
                                    messagesRV.smoothScrollToPosition(messagesRV.getAdapter().getItemCount());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                });


                messages = HelpingFunctions.getAllMessages(usernameSender, usernameReceiver);
                if (messages.size() < 30) {
                    messagesCount = messages.size();
                } else {
                    messagesCount = HelpingFunctions.getNumberOfMessages(usernameSender, usernameReceiver);
                }


                // Recycler View
                messagesRV = findViewById(R.id.messagesRV);
                messageAdapter = new MessageAdapter(MessagingPage.this, messages);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usernameTV.setText(usernameReceiver);
                        messageET.requestFocus();

                        messagesRV.setLayoutManager(new LinearLayoutManager(MessagingPage.this));
                        messagesRV.setAdapter(messageAdapter);

                        messagesRV.smoothScrollToPosition(messagesRV.getAdapter().getItemCount());

                    }
                });

            }
        }).start();
    }

    private void checkForNewMessages() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final int number = HelpingFunctions.getNumberOfMessages(usernameSender, usernameReceiver);
                    if (number != messagesCount) {
                        messages.addAll(HelpingFunctions.getAllNewMessages(usernameSender, usernameReceiver, String.valueOf(number - messagesCount)));

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("username", usernameReceiver);
                        setResult(Activity.RESULT_OK,  returnIntent);

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (number != messagesCount) {
                                messageAdapter.notifyDataSetChanged();
                                messagesRV.smoothScrollToPosition(messagesRV.getAdapter().getItemCount());
                                messagesCount = messages.size();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView timeTV;
        TextView messageTV;
        RelativeLayout relativeLayout; // message as a whole

        SenderViewHolder(View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.timeTV);
            messageTV = itemView.findViewById(R.id.messageTV);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView timeTV;
        TextView messageTV;
        RelativeLayout relativeLayout; // message as a whole

        ReceiverViewHolder(View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.timeTV);
            messageTV = itemView.findViewById(R.id.messageTV);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }

    class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_SENDER = 0, VIEW_TYPE_RECEIVER = 1;
        Activity activity;
        Context context;
        ArrayList<TextMessage> messages;


        MessageAdapter(Activity activity, ArrayList<TextMessage> messages) {
            this.activity = activity;
            this.context = activity.getApplicationContext();
            this.messages = messages;

        }

        @Override
        public int getItemViewType(int position) {

            if (messages.get(position).getType() == accountType) {
                return VIEW_TYPE_SENDER;
            } else {
                return VIEW_TYPE_RECEIVER;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == VIEW_TYPE_SENDER) {

                View view = LayoutInflater.from(activity).inflate(R.layout.list_message_sender, parent, false);
                return new SenderViewHolder(view);

            } else if (viewType == VIEW_TYPE_RECEIVER) {

                View view = LayoutInflater.from(activity).inflate(R.layout.list_message_receiver, parent, false);
                return new ReceiverViewHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final TextMessage message = messages.get(position);

            if (holder instanceof SenderViewHolder) {
                final SenderViewHolder viewHolder = (SenderViewHolder) holder;
                viewHolder.messageTV.setText(message.getMessage());
                viewHolder.timeTV.setText(message.getDate());
            } else {
                final ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.messageTV.setText(message.getMessage());
                viewHolder.timeTV.setText(message.getDate());
            }

        }


        @Override
        public int getItemCount() {
            return messages.size();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
