package com.example.webview;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FirebaseMessageReceiver extends FirebaseMessagingService {


    private static final String CHANNEL_ID = "my_channel_id";
    OkHttpClient client=new OkHttpClient ();
    String head,body;
    private static String url="http://192.168.31.126/DatabaseConnection/register.php";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle FCM messages here.
//        String title = remoteMessage.getNotification().getTitle();
//        String message = remoteMessage.getNotification().getBody();
        String urL = remoteMessage.getData().get("url");
        fetchMessage ( url );

        sendNotification(head, body, urL);
    }

    private void sendNotification(String title, String messageBody, String url) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", url);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = CHANNEL_ID;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }



    private void fetchMessage(String url) {
        Request request = new Request.Builder ()
                .url ( url )
                .build ();

        client.newCall ( request ).enqueue ( new Callback () {

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful ()) {
                    final String jsonResponse = response.body ().string ();


                        try {
                            JSONObject jsonObject = new JSONObject ( jsonResponse );
                            head = jsonObject.getString ( "head" );
                            body = jsonObject.getString ( "body" );
//                            tvMessage.setText ( message );
                        } catch (JSONException e) {
                            e.printStackTrace ();
//                            tvMessage.setText ( "Failed to parse message" );
                        }

                } else {
//                    runOnUiThread ( () -> tvMessage.setText ( "Failed to load message" ) );
                }
            }
        } );
    }
}
