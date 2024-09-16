package com.example.webview;


import static com.example.webview.NotificationChannelHelper.channelId;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

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

        // add the intent or webpage on click on notification

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData( Uri.parse("https://edschool.in/demo/notification.php"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);



        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder ( this, channelId )
                .setSmallIcon ( R.mipmap.applogo )
                .setContentTitle ( title )
                .setContentText ( message )
                .setAutoCancel ( true )
                .addAction(android.R.drawable.ic_menu_view, "Tap to View", pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)) // For long messages
                .setPriority ( NotificationCompat.PRIORITY_HIGH );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( (Activity) getApplicationContext (), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
            }
        }

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from ( this );
        NotificationManager manager = (NotificationManager) getApplicationContext ().getSystemService ( Context.NOTIFICATION_SERVICE );


        if (ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED) {
            Log.d ("Permission Error","Notification permission denied ");
        }
        notificationManager.notify ( 12345, builder.build () );
    }


    }




