package com.licence.pocketteacher.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.TextMessage;
import com.licence.pocketteacher.miscellaneous.FirebaseMessagingService;
import com.licence.pocketteacher.miscellaneous.HelpingFunctions;
import com.licence.pocketteacher.student.home.FragmentHomeS;
import com.licence.pocketteacher.student.home.LoadMore;
import com.licence.pocketteacher.student.home.SeeStudentStudent;
import com.licence.pocketteacher.student.search.SeeTeacher;
import com.licence.pocketteacher.teacher.profile.followers.SeeStudent;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagingPage extends AppCompatActivity {

    private EditText messageET;
    private ImageView sendMessageIV;
    private RecyclerView messagesRV;
    private ArrayList<TextMessage> displayedMessages, messages;
    private TextMessage lastSenderMessage, lastMessage;
    private MessageAdapter messageAdapter;
    private RelativeLayout.LayoutParams paramsShowTimeRelative, paramsHideTimeRelative;

    private String usernameSender, usernameReceiver, receiverImageBase64;
    private int senderType, receiverType, messagesCount, blocked, lastPos = 0;
    private boolean keepChecking = true;
    private boolean isDisplayed = true;

    public static String talkingUsername = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_page);

        getIntentData();
        initiateComponents();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            usernameSender = bundle.getString("username_sender");
            senderType = bundle.getInt("type");
            usernameReceiver = bundle.getString("username_receiver");
            talkingUsername = usernameReceiver;
            blocked = bundle.getInt("blocked");
            receiverImageBase64 = bundle.getString("image");

        }
    }

    private void initiateComponents() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                // Coming from Notifications
                if (getIntent().getExtras().getString("flag") != null) {
                    List<Integer> positions = new ArrayList<>();
                    String messaging_id = getIntent().getExtras().getString("messaging_id");

                    for (TextMessage text : FirebaseMessagingService.newMessages) {
                        if (text.getMessagingId().equals(messaging_id)) {
                            positions.add(FirebaseMessagingService.newMessages.indexOf(text));
                        }
                    }

                    for (int i = positions.size() - 1; i >= 0; i--) {
                        FirebaseMessagingService.newMessages.remove(i);
                    }

                }


                // Messages date format
                paramsShowTimeRelative = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                paramsShowTimeRelative.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                paramsShowTimeRelative.setMargins(0, 5, 0, 5);

                paramsHideTimeRelative = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                paramsHideTimeRelative.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                paramsHideTimeRelative.setMargins(0, 0, 0, 0);

                // mark unseen messages as seen
                if (Integer.parseInt(HelpingFunctions.getAccountType(usernameReceiver)) == HelpingFunctions.getLastMessage(usernameSender, usernameReceiver).getType()) {
                    HelpingFunctions.markMessagesAsSeen(usernameSender, usernameReceiver);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("username", usernameReceiver);
                    setResult(Activity.RESULT_OK, returnIntent);
                }


                // For delivered/seen message - THESE CAN BE NULL IF NO MESSAGE EXISTS OR NO SENDER MESSAGE EXISTS
                lastSenderMessage = HelpingFunctions.getLastMessageSent(usernameSender, usernameReceiver);
                lastMessage = HelpingFunctions.getLastMessage(usernameSender, usernameReceiver);

                // Data declarations
                messageET = findViewById(R.id.messageET);
                final TextView usernameTV = findViewById(R.id.usernameTV);
                receiverType = Integer.parseInt(HelpingFunctions.getAccountType(usernameReceiver));


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
                                        profileIV.setImageResource(R.drawable.profile_picture_neutral);
                                        break;
                                }
                            } else {
                                profileIV.setImageBitmap(HelpingFunctions.convertBase64toImage(receiverImageBase64));
                            }

                            profileIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openProfile();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Send Message icon
                sendMessageIV = findViewById(R.id.sendMessageIV);
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessage();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Messages
                displayedMessages = new ArrayList<>();
                messages = HelpingFunctions.getAllMessages(usernameSender, usernameReceiver);
                messagesCount = HelpingFunctions.getNumberOfMessages(usernameSender, usernameReceiver);
                getNextMessages();


                // Recycler View
                messagesRV = findViewById(R.id.messagesRV);
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessagingPage.this);
                linearLayoutManager.setReverseLayout(true);
                messageAdapter = new MessageAdapter(messagesRV, linearLayoutManager, MessagingPage.this, displayedMessages);


                // Run on UI
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            messagesRV.setHasFixedSize(true);
                            messagesRV.setLayoutManager(linearLayoutManager);
                            messagesRV.setAdapter(messageAdapter);

                            messageET.requestFocus();
                            usernameTV.setText(usernameReceiver);
                            usernameTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openProfile();
                                }
                            });


                            messageAdapter.setLoadMore(new LoadMore() {
                                @Override
                                public void onLoadMore() {

                                    if (displayedMessages.size() < messages.size()) {
                                        displayedMessages.add(null);
                                        messageAdapter.notifyItemInserted(displayedMessages.size() - 1);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                displayedMessages.remove(displayedMessages.size() - 1);
                                                messageAdapter.notifyItemRemoved(displayedMessages.size());

                                                // load 30 more messages
                                                getNextMessages();

                                                messageAdapter.notifyDataSetChanged();
                                                messageAdapter.setLoaded();
                                            }
                                        }, 1000);
                                    }
                                }
                            });


                            messagesRV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    messagesRV.smoothScrollToPosition(0);
                                    messageET.performClick(); // to ensure scrolling for some casese when it didnt!
                                    //At this point the layout is complete and the
                                    //dimensions of recyclerView and any child views are known.
                                    //Remove listener after changed RecyclerView's height to prevent infinite loop
                                    messagesRV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            });

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void sendMessage(){
        // Blocked status
        if (blocked == 1) {
            findViewById(R.id.messageLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.blockedAccountTV).setVisibility(View.VISIBLE);

        }

        sendMessageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (HelpingFunctions.isEditTextEmpty(messageET)) {
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (!HelpingFunctions.isConnected(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "An internet connection is required.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final String textMessage = messageET.getText().toString();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageET.setText("");
                            }
                        });

                        // send message
                        HelpingFunctions.sendMessage(usernameSender, usernameReceiver, textMessage);
                        // send notification
                        HelpingFunctions.sendNotificationMessage(usernameSender, usernameReceiver, textMessage);


                        String oldDt;
                        try {
                            oldDt = lastMessage.getDate();
                        } catch (Exception e) {
                            oldDt = "";
                        }


                        final String oldDate = oldDt;

                        lastSenderMessage = HelpingFunctions.getLastMessageSent(usernameSender, usernameReceiver);
                        lastMessage = lastSenderMessage;


                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("username", usernameReceiver);
                        setResult(Activity.RESULT_OK, returnIntent);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!oldDate.equals(lastSenderMessage.getDate())) {
                                    messages.add(0, new TextMessage(lastSenderMessage.getFullDate()));
                                    displayedMessages.add(0, new TextMessage(lastSenderMessage.getFullDate()));
                                    messageAdapter.notifyItemInserted(0);
                                }
                                messages.add(0, new TextMessage(usernameSender, textMessage, senderType, usernameReceiver));
                                displayedMessages.add(0, new TextMessage(usernameSender, textMessage, senderType, usernameReceiver));
                                messagesCount++;

                                messageAdapter.sendMessage();
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
                            messagesRV.smoothScrollToPosition(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    private void checkForNewMessages() {

        final Context context = getApplicationContext();

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (keepChecking) {

                        if (!isDisplayed) {
                            return;
                        }

                        try {
                            Thread.sleep(1500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!HelpingFunctions.isConnected(context)) {
                            continue;
                        }

                        // check for new seen status
                        if (lastSenderMessage.getSeenStatus() == 0) {
                            if (HelpingFunctions.getLastMessageSent(usernameSender, usernameReceiver).getSeenStatus() == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageAdapter.changeSeenStatus();
                                    }
                                });

                                if (!HelpingFunctions.isConnected(context)) {
                                    continue;
                                }
                            }
                        }


                        // check for new messages
                        final int number = HelpingFunctions.getNumberOfMessages(usernameSender, usernameReceiver);
                        if (number != messagesCount) {

                            // check for internet connection
                            if (!HelpingFunctions.isConnected(context)) {
                                continue;
                            }

                            // obtain the new messages
                            final ArrayList<TextMessage> newMessages = HelpingFunctions.getAllNewMessages(usernameSender, usernameReceiver, String.valueOf(number - messagesCount));
                            if (newMessages.size() + messages.size() > messagesCount) {
                                // mark them as read
                                HelpingFunctions.markMessagesAsSeen(usernameSender, usernameReceiver);

                                // ensure updating of the information when going back
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("username", usernameReceiver);
                                setResult(Activity.RESULT_OK, returnIntent);

                                // update fields in RV
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            if (!(lastMessage.getDate()).equals(newMessages.get(0).getDate())) {
                                                messages.add(0, new TextMessage(newMessages.get(0).getFullDate()));
                                                displayedMessages.add(0, new TextMessage(newMessages.get(0).getFullDate()));
                                                messageAdapter.notifyItemInserted(0);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        for (TextMessage message : newMessages) {
                                            if (!message.getUsername().equals(usernameSender)) {
                                                messages.add(0, message);
                                                displayedMessages.add(0, message);
                                                messageAdapter.notifyItemInserted(0);
                                            }
                                        }
                                        messagesCount = number;

                                        lastMessage = HelpingFunctions.getLastMessage(usernameSender, usernameReceiver);
                                        try {
                                            if (!(lastMessage.getDate()).equals(lastSenderMessage.getDate())) {
                                                lastSenderMessage.setDate(lastMessage.getDate());
                                            }
                                        } catch (Exception e) {
                                            lastSenderMessage.setDate(lastMessage.getDate());
                                        }

                                        messagesRV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                            @Override
                                            public void onGlobalLayout() {
                                                messagesRV.smoothScrollToPosition(0);
                                                messageET.performClick();
                                                //At this point the layout is complete and the
                                                //dimensions of recyclerView and any child views are known.
                                                //Remove listener after changed RecyclerView's height to prevent infinite loop
                                                messagesRV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                            }
                                        });
                                        messageET.performClick();
                                    }
                                });
                            }
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNextMessages() {

        int number;
        if (messages.size() - displayedMessages.size() >= 30) {
            number = 30;
        } else {
            number = messages.size() - displayedMessages.size();
        }

        for (int i = lastPos; i < lastPos + number; i++) {
            displayedMessages.add(messages.get(i));
        }
        lastPos += number;
    }

    private void openProfile(){

        if(senderType == 1 && receiverType == 1){
            // Opening a teacher profile BY A teacher is not available yet
            return;
        }

        keepChecking = false;
        talkingUsername = null;

        if(senderType == 0){
            if(receiverType == 0){
                Intent intent = new Intent(getApplicationContext(), SeeStudentStudent.class);
                intent.putExtra("username", usernameReceiver);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }else{
                Intent intent = new Intent(getApplicationContext(), SeeTeacher.class);
                intent.putExtra("username", usernameReceiver);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
            }
        }else{
            Intent intent = new Intent(getApplicationContext(), SeeStudent.class);
            intent.putExtra("username", usernameReceiver);
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_no_slide);
        }
    }




    /*                           *** Messages adapters and aiding classes ***                     */

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        TextView dateTV;

        DateViewHolder(View itemView) {
            super(itemView);

            dateTV = itemView.findViewById(R.id.dateTV);
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

    // A D A P T E R
    class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_SENDER = 0, VIEW_TYPE_RECEIVER = 1, VIEW_TYPE_LOADING = 2, VIEW_TYPE_DATE = 3;
        Activity activity;
        Context context;
        ArrayList<TextMessage> messages;
        LinearLayoutManager linearLayoutManager;

        LoadMore loadMore;
        boolean isLoading;
        int visibleThreshold = 30;
        int lastVisibleItem, totalItemCount;


        MessageAdapter(RecyclerView recyclerView, final LinearLayoutManager linearLayoutManager, Activity activity, ArrayList<TextMessage> messages) {
            this.activity = activity;
            this.context = activity.getApplicationContext();
            this.messages = messages;
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

            if (messages.get(position) == null) {
                return VIEW_TYPE_LOADING;
            }

            if (messages.get(position).getUsername() == null) {
                return VIEW_TYPE_DATE;
            }

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
            } else if (viewType == VIEW_TYPE_DATE) {
                View view = LayoutInflater.from(activity).inflate(R.layout.list_message_date, parent, false);
                return new DateViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(activity).inflate(R.layout.load_more_progress_bar, parent, false);
                return new LoadingViewHolder(view);
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
                        showHideTime(message);
                    }
                });

                if(message.isShowTime()){
                    viewHolder.timeTV.setText(message.getHourAndMinutes());
                    viewHolder.timeTV.setTextSize(14);
                    viewHolder.timeTV.setLayoutParams(paramsShowTimeRelative);
                    viewHolder.updatesTV.setTextSize(12);
                    viewHolder.updatesTV.setText(message.getMessageStatusForDisplay());
                    viewHolder.updatesTV.setVisibility(View.VISIBLE);

                }else{
                    viewHolder.timeTV.setText("");
                    viewHolder.timeTV.setTextSize(1);
                    viewHolder.timeTV.setLayoutParams(paramsHideTimeRelative);
                    if (!message.getHourMinutesAndSeconds().equals(lastSenderMessage.getHourMinutesAndSeconds())) {
                        viewHolder.updatesTV.setTextSize(1);
                        viewHolder.updatesTV.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.updatesTV.setTextSize(12);
                        viewHolder.updatesTV.setText(message.getMessageStatusForDisplay());
                        viewHolder.updatesTV.setVisibility(View.VISIBLE);
                    }


                }
            }

            if (holder instanceof ReceiverViewHolder) {

                final ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.messageTV.setText(message.getMessage());
                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showHideTime(message);
                    }
                });


                if(message.isShowTime()){
                    viewHolder.timeTV.setText(message.getHourAndMinutes());
                    viewHolder.timeTV.setTextSize(14);
                    viewHolder.timeTV.setLayoutParams(paramsShowTimeRelative);
                    viewHolder.updatesTV.setTextSize(12);
                    viewHolder.updatesTV.setText(message.getMessageStatusForDisplay());
                    viewHolder.updatesTV.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.timeTV.setText("");
                    viewHolder.timeTV.setTextSize(1);
                    viewHolder.timeTV.setLayoutParams(paramsHideTimeRelative);
                    viewHolder.updatesTV.setTextSize(1);
                    viewHolder.updatesTV.setVisibility(View.INVISIBLE);
                }

            }

            if (holder instanceof DateViewHolder) {
                final DateViewHolder viewHolder = (DateViewHolder) holder;
                viewHolder.dateTV.setText(HelpingFunctions.getDateText(message.getDate()));
            }

            if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }


        void changeSeenStatus() {

            for (TextMessage message : messages) {
                if (message.getFullDate().equals(lastSenderMessage.getFullDate())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());


                    lastSenderMessage.setSeenStatus(1);
                    lastSenderMessage.setDateSeen(currentDateandTime);

                    messages.get(messages.indexOf(message)).setSeenStatus(1);
                    messages.get(messages.indexOf(message)).setDateSeen(currentDateandTime);
                    notifyItemChanged(messages.indexOf(message));
                    return;
                }
            }
        }

        void sendMessage() {

            notifyItemInserted(0);

            for (int i = 1; i < getItemCount(); i++) {
                if (messages.get(i).getUsername() != null) {
                    if (messages.get(i).getUsername().equals(usernameSender)) {
                        notifyItemChanged(i);
                        break;
                    }
                }
            }

            messageET.performClick();
        }

        void showHideTime(TextMessage message){
            int position = messages.indexOf(message);

            if(message.isShowTime()){
                messages.get(position).setShowTime(false);
            }else{
                messages.get(position).setShowTime(true);
            }

            notifyItemChanged(position);
        }


        @Override
        public int getItemCount() {
            return messages.size();
        }

        void setLoaded() {
            isLoading = false;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            keepChecking = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        talkingUsername = null;

        keepChecking = false;

        finish();
        overridePendingTransition(R.anim.anim_no_slide, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onStop() {
        super.onStop();

        talkingUsername = null;

        // When minimizing the app - to stop the thread for looking for internet connection
        isDisplayed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(usernameReceiver != null){
            talkingUsername = usernameReceiver;
        }

        // When maximizing the app - to start the thread for looking for internet connection
        isDisplayed = true;
        checkForNewMessages();
    }


}
