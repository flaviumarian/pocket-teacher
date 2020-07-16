package com.licence.pocketteacher.miscellaneous;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;
import com.licence.pocketteacher.aiding_classes.TextMessage;
import com.licence.pocketteacher.messaging.MessagingPage;
import com.licence.pocketteacher.student.MainPageS;
import com.licence.pocketteacher.student.search.SeePostStudent;
import com.licence.pocketteacher.student.search.SeeTeacher;
import com.licence.pocketteacher.teacher.MainPageT;
import com.licence.pocketteacher.teacher.folder.subject.files.SeePostTeacher;
import com.licence.pocketteacher.teacher.profile.followers.SeeStudent;
import com.licence.pocketteacher.teacher.updates.follow_requests.SeeFollowRequests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.licence.pocketteacher.ApplicationWrapper.CHANNEL_FUNCTIONAL_NOTIFICATIONS;
import static com.licence.pocketteacher.ApplicationWrapper.CHANNEL_MESSAGING_NOTIFICATIONS;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private NotificationManagerCompat notificationManager;
    public static List<TextMessage> newMessages = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();

        // Defined in the ApplicationWrapper class
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // 0 - New message
        // 1 - Open post Student
        // 2 - Open Teacher profile
        // 3 - Open post Teacher
        // 4 - Open follow requests
        // 5 - Open Student profile
        String flag = remoteMessage.getData().get("flag");


        switch (flag) {
            case "0":
                newMessages.add(new TextMessage(remoteMessage.getData().get("group"), remoteMessage.getData().get("message"), remoteMessage.getData().get("messaging_id")));
                sendNewMessageNotification(this.getApplicationContext(), remoteMessage.getData().get("messaging_id"),
                        remoteMessage.getData().get("group"));
                break;

            case "1":
                sendNotificationToOpenPostStudent(remoteMessage.getData().get("message"),
                        remoteMessage.getData().get("username_commenter"),
                        remoteMessage.getData().get("username_poster"),
                        remoteMessage.getData().get("subject"),
                        remoteMessage.getData().get("folder"),
                        remoteMessage.getData().get("title"));
                break;

            case "2":
                sendNotificationToOpenTeacherProfile(remoteMessage.getData().get("message"),
                        remoteMessage.getData().get("username_teacher"));
                break;

            case "3":
                sendNotificationToOpenPostTeacher(remoteMessage.getData().get("message"),
                        remoteMessage.getData().get("username_commenter"),
                        remoteMessage.getData().get("username_poster"),
                        remoteMessage.getData().get("subject"),
                        remoteMessage.getData().get("folder"),
                        remoteMessage.getData().get("title"));
                break;
            case "4":
                sendNotificationToOpenFollowRequests(remoteMessage.getData().get("message"),
                        remoteMessage.getData().get("username_commenter"));
                break;
            case "5":
                sendNotificationToOpenStudentProfile(remoteMessage.getData().get("message"),
                        remoteMessage.getData().get("username_commenter"));
                break;
        }

    }

    // F L A G   0
    public void sendNewMessageNotification(Context context, String messaging_id, String group) {

        if(MessagingPage.talkingUsername != null){
            if(MessagingPage.talkingUsername.equals(group)){
                Log.i("array1", " nu am trimis");
                return;
            }
        }

        // Intent for when tapping on the notification
        Intent notificationIntent;
        if (MainPageS.student != null) {
            // ACTIVE STUDENT RECEIVED THE NOTIFICATION
            notificationIntent = new Intent(context, MessagingPage.class);
            notificationIntent.putExtra("flag", "0");
            notificationIntent.putExtra("messaging_id", messaging_id);
            notificationIntent.putExtra("username_sender", MainPageS.student.getUsername());
            notificationIntent.putExtra("type", 0);
            notificationIntent.putExtra("username_receiver", group);
            notificationIntent.putExtra("blocked", 0);
            notificationIntent.putExtra("image", HelpingFunctions.getProfileImageBasedOnUsername(group));
        } else if (MainPageT.teacher != null) {
            // ACTIVE TEACHER RECEIVED THE NOTIFICATION
            notificationIntent = new Intent(context, MessagingPage.class);
            notificationIntent.putExtra("flag", "0");
            notificationIntent.putExtra("messaging_id", messaging_id);
            notificationIntent.putExtra("username_sender", MainPageT.teacher.getUsername());
            notificationIntent.putExtra("type", 1);
            notificationIntent.putExtra("username_receiver", group);
            notificationIntent.putExtra("blocked", 0);
            notificationIntent.putExtra("image", HelpingFunctions.getProfileImageBasedOnUsername(group));
        } else {
            // NO ACTIVE USER
            notificationIntent = new Intent(context, OpeningPage.class);
            notificationIntent.putExtra("flag", "0");
            notificationIntent.putExtra("messaging_id", messaging_id);
            notificationIntent.putExtra("messaging_username", group);
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(messaging_id), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        // Messaging style
        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle("Me");


        for (TextMessage textMessage : newMessages) {
            if(textMessage.getMessagingId().equals(messaging_id)) {
                NotificationCompat.MessagingStyle.Message notificationMessage =
                        new NotificationCompat.MessagingStyle.Message(
                                textMessage.getMessage(),
                                textMessage.getTimestamp(),
                                textMessage.getUsername()
                        );
                messagingStyle.addMessage(notificationMessage);
            }
        }

        // Building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_MESSAGING_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(context.getResources().getColor(R.color.lightGrey))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(messagingStyle)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        // Notify
        notificationManager.notify(Integer.parseInt(messaging_id), notificationBuilder.build());

    }

    // F L A G   1
    private void sendNotificationToOpenPostStudent(String message, String usernameCommenter, String usernamePoster, String subject, String folder, String title) {


        // Title of notification
        Spannable sb = new SpannableString(usernameCommenter);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Intent for when tapping on the notification
        Intent notificationIntent;
        if (MainPageS.student == null) {
            notificationIntent = new Intent(this, OpeningPage.class);
        } else {
            notificationIntent = new Intent(this, SeePostStudent.class);
        }

        notificationIntent.putExtra("flag", "1");
        notificationIntent.putExtra("usernameTeacher", usernamePoster);
        notificationIntent.putExtra("folderName", folder);
        notificationIntent.putExtra("subject", subject);
        notificationIntent.putExtra("fileName", title);

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_FUNCTIONAL_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.lightGrey))
                .setContentTitle(sb)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message))
                .setContentIntent(pendingIntent);


        // Notify
        notificationManager.notify(new Random().nextInt(60000), notificationBuilder.build());
    }

    // F L A G   2
    private void sendNotificationToOpenTeacherProfile(String message, String usernameTeacher) {
        // Title of notification
        Spannable sb = new SpannableString(usernameTeacher);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Intent for when tapping on the notification
        Intent notificationIntent;
        if (MainPageS.student == null) {
            notificationIntent = new Intent(this, OpeningPage.class);
        } else {
            notificationIntent = new Intent(this, SeeTeacher.class);
        }
        notificationIntent.putExtra("flag", "2");
        notificationIntent.putExtra("username", usernameTeacher);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_FUNCTIONAL_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.lightGrey))
                .setContentTitle(sb)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message))
                .setContentIntent(pendingIntent);


        // Notify
        notificationManager.notify(new Random().nextInt(60000), notificationBuilder.build());
    }

    // F L A G   3
    private void sendNotificationToOpenPostTeacher(String message, String usernameCommenter, String usernamePoster, String subject, String folder, String title) {


        // Title of notification
        Spannable sb = new SpannableString(usernameCommenter);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        // Intent for when tapping on the notification
        Intent notificationIntent;
        if (MainPageT.teacher == null) {
            notificationIntent = new Intent(this, OpeningPage.class);
        } else {
            notificationIntent = new Intent(this, SeePostTeacher.class);
        }

        notificationIntent.putExtra("flag", "3");
        notificationIntent.putExtra("fileName", title);
        notificationIntent.putExtra("fromNotifications", true);
        notificationIntent.putExtra("subjectName", subject);
        notificationIntent.putExtra("folderName", folder);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_FUNCTIONAL_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.lightGrey))
                .setContentTitle(sb)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message))
                .setContentIntent(pendingIntent);


        // Notify
        notificationManager.notify(new Random().nextInt(60000), notificationBuilder.build());
    }

    // F L A G   4
    private void sendNotificationToOpenFollowRequests(String message, String usernameCommenter) {
        // Title of notification
        Spannable sb = new SpannableString(usernameCommenter);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        // Intent for when tapping on the notification
        Intent notificationIntent;
        if (MainPageT.teacher == null) {
            notificationIntent = new Intent(this, OpeningPage.class);
        } else {
            notificationIntent = new Intent(this, SeeFollowRequests.class);
        }
        notificationIntent.putExtra("flag", "4");
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_FUNCTIONAL_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.lightGrey))
                .setContentTitle(sb)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message))
                .setContentIntent(pendingIntent);


        // Notify
        notificationManager.notify(new Random().nextInt(60000), notificationBuilder.build());
    }

    // F L A G   5
    private void sendNotificationToOpenStudentProfile(String message, String usernameCommenter) {
        // Title of notification
        Spannable sb = new SpannableString(usernameCommenter);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        // Intent for when tapping on the notification
        Intent notificationIntent;
        if (MainPageT.teacher == null) {
            notificationIntent = new Intent(this, OpeningPage.class);
        } else {
            notificationIntent = new Intent(this, SeeStudent.class);
        }
        notificationIntent.putExtra("username", usernameCommenter);
        notificationIntent.putExtra("flag", "5");
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_FUNCTIONAL_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.lightGrey))
                .setContentTitle(sb)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message))
                .setContentIntent(pendingIntent);


        // Notify
        notificationManager.notify(new Random().nextInt(60000), notificationBuilder.build());
    }


}
