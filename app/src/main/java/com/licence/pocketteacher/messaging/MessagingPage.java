package com.licence.pocketteacher.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.TextMessage;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

import java.util.ArrayList;

public class MessagingPage extends AppCompatActivity {

    private EditText messageET;
    private RecyclerView messagesRV;
    private ArrayList<TextMessage> messages;
    private MessageAdapter messageAdapter;

    private String usernameSender, usernameReceiver, receiverImageBase64;
    private int senderType, messagesCount, blocked;
    private boolean keepChecking = true;
    private TextMessage lastSenderMessage;

    private LinearLayout.LayoutParams paramsShowTime, paramsHideTime;


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
            senderType = bundle.getInt("type");
            usernameReceiver = bundle.getString("username_receiver");
            blocked = bundle.getInt("blocked");
            receiverImageBase64 = bundle.getString("image");
        }
    }

    private void initiateComponents() {

        // Messages date format
        paramsShowTime = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsShowTime.gravity = Gravity.CENTER;
        paramsShowTime.setMargins(20, 0, 20, 0);

        paramsHideTime = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsHideTime.setMargins(0, 0, 0, 0);



        new Thread(new Runnable() {
            @Override
            public void run() {

                // For delivered/seen message
                lastSenderMessage = HelpingFunctions.getLastSenderMessageDate(usernameSender, usernameReceiver);


                // Data declarations
                messageET = findViewById(R.id.messageET);
                final TextView usernameTV = findViewById(R.id.usernameTV);


                // Back ImageView
                ImageView backIV = findViewById(R.id.backIV);
                backIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                // Receiver profile image
                final ImageView profileIV = findViewById(R.id.profilePictureIV);
                final String receiverGender = HelpingFunctions.getGenderBasedOnUsername(usernameReceiver);
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (receiverImageBase64.equals("")) {
                                switch (receiverGender) {
                                    case "0":
                                        profileIV.setImageResource(R.drawable.profile_picture_male);
                                        break;
                                    case "1":
                                        profileIV.setImageResource(R.drawable.profile_picture_female);
                                        break;
                                    case "2":
                                        profileIV.setImageResource(0);
                                        break;
                                }
                            } else {
                                profileIV.setImageBitmap(HelpingFunctions.convertBase64toImage(receiverImageBase64));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Blocked status
                if (blocked == 1) {
                    findViewById(R.id.messageLayout).setVisibility(View.INVISIBLE);
                    findViewById(R.id.blockedAccountTV).setVisibility(View.VISIBLE);
                }


                // Send Message icon
                final ImageView sendMessageIV = findViewById(R.id.sendMessageIV);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessageIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (HelpingFunctions.isEditTextEmpty(messageET)) {
                                        return;
                                    }

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if(!HelpingFunctions.isConnected(getApplicationContext())){
                                                Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            HelpingFunctions.sendMessage(usernameSender, usernameReceiver, messageET.getText().toString());
                                            lastSenderMessage = HelpingFunctions.getLastSenderMessageDate(usernameSender, usernameReceiver);


                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra("username", usernameReceiver);
                                            setResult(Activity.RESULT_OK, returnIntent);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    messages.add(new TextMessage(usernameSender, messageET.getText().toString(), senderType, usernameReceiver));
                                                    messagesCount++;
                                                    messageET.setText("");
                                                    messagesRV.smoothScrollToPosition(messagesRV.getAdapter().getItemCount());

                                                    messageAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }).start();
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
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


                messages = HelpingFunctions.getAllMessages(usernameSender, usernameReceiver);
                if (messages.size() < 30) {
                    messagesCount = messages.size();
                } else {
                    messagesCount = HelpingFunctions.getNumberOfMessages(usernameSender, usernameReceiver);
                }

                // Recycler View
                messagesRV = findViewById(R.id.messagesRV);
                messageAdapter = new MessageAdapter(MessagingPage.this, messages);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            messagesRV.setLayoutManager(new LinearLayoutManager(MessagingPage.this));
                            messagesRV.setAdapter(messageAdapter);

                            messagesRV.smoothScrollToPosition(messagesRV.getAdapter().getItemCount());


                            messageET.requestFocus();
                            usernameTV.setText(usernameReceiver);


                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    private void checkForNewMessages() {

        final Context context = getApplicationContext();

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (keepChecking) {

                        try {
                            Thread.sleep(1500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!HelpingFunctions.isConnected(context)) {
                            continue;
                        }
                        final int number = HelpingFunctions.getNumberOfMessages(usernameSender, usernameReceiver);
                        if (number != messagesCount) {

                            if (!HelpingFunctions.isConnected(context)) {
                                continue;
                            }
                            messages.addAll(HelpingFunctions.getAllNewMessages(usernameSender, usernameReceiver, String.valueOf(number - messagesCount)));

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("username", usernameReceiver);
                            setResult(Activity.RESULT_OK, returnIntent);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView timeTV;
        TextView messageTV;
        TextView updatesTV;
        RelativeLayout relativeLayout; // message as a whole
        LinearLayout linearLayout;
        CardView messageC;

        SenderViewHolder(View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.timeTV);
            messageTV = itemView.findViewById(R.id.messageTV);
            updatesTV = itemView.findViewById(R.id.updatesTV);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            messageC = itemView.findViewById(R.id.messageC);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView timeTV;
        TextView messageTV;
        TextView updatesTV;
        RelativeLayout relativeLayout; // message as a whole
        LinearLayout linearLayout;
        CardView messageC;

        ReceiverViewHolder(View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.timeTV);
            messageTV = itemView.findViewById(R.id.messageTV);
            updatesTV = itemView.findViewById(R.id.updatesTV);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            messageC = itemView.findViewById(R.id.messageC);
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

            if (messages.get(position).getUsername().equals(usernameSender)) {
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
                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (viewHolder.timeTV.getText().length() > 0) {
                            viewHolder.timeTV.setText("");
                            viewHolder.timeTV.setLayoutParams(paramsHideTime);
                        } else {
                            viewHolder.timeTV.setText(message.getDate());
                            viewHolder.timeTV.setLayoutParams(paramsShowTime);
                        }
                    }
                });

                try {

                    if (!message.getExactHour().equals(lastSenderMessage.getExactHour())) {
                        viewHolder.updatesTV.setTextSize(1);
                        viewHolder.updatesTV.setVisibility(View.INVISIBLE);
                    }else{
                        viewHolder.updatesTV.setTextSize(16);
                        viewHolder.updatesTV.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }


            } else {
                final ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.messageTV.setText(message.getMessage());
                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.timeTV.getText().length() > 0) {
                            viewHolder.timeTV.setText("");
                            viewHolder.timeTV.setLayoutParams(paramsHideTime);
                        } else {
                            viewHolder.timeTV.setText(message.getDate());
                            viewHolder.timeTV.setLayoutParams(paramsShowTime);
                        }
                    }
                });

                try {
                    viewHolder.updatesTV.setTextSize(1);
                    viewHolder.updatesTV.setVisibility(View.INVISIBLE);
//                    ((ViewManager) viewHolder.updatesTV.getParent()).removeView(viewHolder.updatesTV);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        keepChecking = false;

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }
}
