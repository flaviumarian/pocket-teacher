package com.licence.pocketteacher.miscellaneous;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.google.firebase.messaging.RemoteMessage;
import com.licence.pocketteacher.OpeningPage;
import com.licence.pocketteacher.R;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String message) {

        String CHANNEL_ID = "pocket_channel";
        String GROUP_NOTIFICATIONS = "Pocket Teacher";
        int NOTIFICATION_ID = new Random().nextInt(60000); // to have unique notifications
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "Pocket_Teacher_channel";
            String Description = "Channel used for notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }

        }
        Spannable sb = new SpannableString("Pocket Teacher");
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        Intent notificationIntent = new Intent(this, OpeningPage.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(sb)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setGroup(GROUP_NOTIFICATIONS)
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID , notificationBuilder.build());
    }
}
