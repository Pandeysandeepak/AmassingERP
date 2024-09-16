package com.example.webview;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannelHelper {
    public static final String channelId = "CHANNEL";
    public static final String channelName = "MyAppNotifications";
    public static final String CHANNEL_DESCRIPTION = "Notifications from my app";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(CHANNEL_DESCRIPTION);
                notificationManager.createNotificationChannel(channel);
                System.out.println ("Notification Channel Id : "+channelId );
                System.out.println ("Notification channel Name : "+ channelName );
            }
        }
    }
}


