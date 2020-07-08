package com.licence.pocketteacher;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;


public class ApplicationWrapper extends Application {
    public static final String CHANNEL_FUNCTIONAL_NOTIFICATIONS = "general";
    public static final String CHANNEL_MESSAGING_NOTIFICATIONS = "messaging";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // G E N E R A L    N O T I F I C A T I O N S
            NotificationChannel channel_functional_notifications = new NotificationChannel(
                    CHANNEL_FUNCTIONAL_NOTIFICATIONS,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel_functional_notifications.enableLights(true);
            channel_functional_notifications.setLightColor(Color.RED);
            channel_functional_notifications.setDescription("This channel is for general purpose notifications.");

            // M E S S A G I N G   N O T I F I C A T I O N S
            NotificationChannel channel_messaging_notifications = new NotificationChannel(
                    CHANNEL_MESSAGING_NOTIFICATIONS,
                    "Messaging Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel_messaging_notifications.enableLights(true);
            channel_messaging_notifications.setLightColor(Color.GREEN);
            channel_messaging_notifications.setDescription("This channel is for message notifications.");


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel_functional_notifications);
            manager.createNotificationChannel(channel_messaging_notifications);



        }
    }
}
