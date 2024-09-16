package com.example.webview;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FCMToken extends FirebaseMessagingService {
    public static String fcm_token;


    public static void generateFCMToken() {
        FirebaseMessaging.getInstance();
        FirebaseMessaging.getInstance ().getToken ()
                .addOnCompleteListener ( task -> {
                    if (!task.isSuccessful ()) {
                        Log.w ( TAG, "Fetching FCM registration token failed", task.getException () );
                        return;
                    }

                    // Get new FCM registration token
                    fcm_token = task.getResult ();
                    Log.d ( TAG, "FCM Token: " + fcm_token );
                    // Send token to your server or save it as needed
                } );
    }
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // Send the new token to your server or save it as needed
    }


}
