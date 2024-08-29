package com.example.webview;


import static com.example.webview.NotificationChannelHelper.channelId;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


@RequiresApi(api = Build.VERSION_CODES.O)
public class FirebaseMessageReceiver extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived ( remoteMessage );
        String head = remoteMessage.getNotification ().getTitle ();
        String body = remoteMessage.getNotification ().getBody ();
        showNotification ( head, body );

    }

    private void showNotification(String title, String message) {
        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder ( this, channelId )
                .setSmallIcon ( R.drawable.logo )
                .setContentTitle ( title )
                .setContentText ( message )
                .setPriority ( NotificationCompat.PRIORITY_HIGH );

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from ( this );
        NotificationManager manager = (NotificationManager) getApplicationContext ().getSystemService ( Context.NOTIFICATION_SERVICE );


        if (ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED) {
            Log.d ("Permission Error","Notification permission denied ");
        }
        notificationManager.notify ( 12345, builder.build () );
    }


    }




