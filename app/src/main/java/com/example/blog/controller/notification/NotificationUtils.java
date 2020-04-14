package com.example.blog.controller.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String CHANNEL1_ID = "ch1";
    public static final String CHANNEL1_NAME = "CHANNEL 1";

    public static final String CHANNEL2_ID = "ch2";
    public static final String CHANNEL2_NAME = "CHANNEL 2";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {

        // create android channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel_1 = new NotificationChannel(CHANNEL1_ID,
                    CHANNEL1_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Sets whether notifications posted to this channel should display notification lights
            channel_1.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            channel_1.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            channel_1.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            channel_1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);


            NotificationChannel channel_2 = new NotificationChannel(CHANNEL2_ID,
                            CHANNEL2_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel_2.enableLights(true);
            channel_2.enableVibration(true);
            channel_2.setLightColor(Color.GRAY);
            channel_2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel_1);
            manager.createNotificationChannel(channel_2);
        }
    }

//    private NotificationManager getManager() {
//        if (mManager == null) {
//            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//        return mManager;
//    }


}