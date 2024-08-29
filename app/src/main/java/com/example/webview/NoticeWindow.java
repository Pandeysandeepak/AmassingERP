package com.example.webview;

import static android.content.ContentValues.TAG;
import static com.example.webview.FCMToken.fcm_token;
import static com.example.webview.MainActivity.accessKey;
import static com.example.webview.NotificationChannelHelper.channelName;

import android.Manifest;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.webview.api.NotificationApi;
import com.example.webview.model.Notification;
import com.example.webview.model.NotificationData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NoticeWindow extends AppCompatActivity {
    private EditText notificationHead, notificationBody;
    private Button publish;
    public static String nHeadData;
    public static String nBodyData;

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private static final String BASE_URL = "http://192.168.31.126/DatabaseConnection/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_window);

        notificationHead = findViewById(R.id.NoticeHeading);
        notificationBody = findViewById(R.id.NoticeBody);

        // Obtain the access token in the background
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(this::getAccessTokenValue);
        executorService.execute(FCMToken::generateFCMToken);

        try {
            // Wait for the access token retrieval to complete
            accessKey = future.get();
            Log.e(TAG, "AccessToken retrieved: " + accessKey);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve access token: " + e.getMessage());
        }

        publish = findViewById(R.id.publish);
        publish.setOnClickListener(v -> {
            nHeadData = notificationHead.getText().toString();
            nBodyData = notificationBody.getText().toString();
            Dexter.withContext ( getApplicationContext () ).withPermission ( Manifest.permission.POST_NOTIFICATIONS ).withListener ( new PermissionListener () {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Toast.makeText ( NoticeWindow.this, "You will receive notification from now onwards", Toast.LENGTH_SHORT ).show ();

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    ActivityCompat.requestPermissions ( NoticeWindow.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.POST_NOTIFICATIONS},1);
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                }
            } ).check ();

            Log.e(TAG, "AccessToken is: " + accessKey);

            // Create and subscribe to notification channel
            NotificationChannelHelper.createNotificationChannel(getApplicationContext());
            FirebaseMessaging.getInstance().subscribeToTopic(channelName);

            // Send notification data to the database
            ExecutorService executorServiceMain = Executors.newSingleThreadExecutor();
            executorServiceMain.execute(this::publishNotice);

            // Send notification to FCM server
            sendNotification(channelName, nHeadData, nBodyData);
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void publishNotice() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("fcm_token", fcm_token);
        builder.add("head", nHeadData);
        builder.add("body", nBodyData);
        RequestBody noticeData = builder.build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(noticeData)
                .build();

        Call call = okHttpClient.newCall(request);

        runOnUiThread(() -> publish.setEnabled(false));  // Disable the button to prevent multiple clicks

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(NoticeWindow.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    publish.setEnabled(true);  // Re-enable the button
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    try {
                        if (response.isSuccessful()) {
                            String resp = Objects.requireNonNull(response.body()).string();
                            Toast.makeText(NoticeWindow.this, "Success: " + resp, Toast.LENGTH_SHORT).show();
                            notificationHead.setText("");
                            notificationBody.setText("");
                        } else {
                            Toast.makeText(NoticeWindow.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(NoticeWindow.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        publish.setEnabled(true);  // Re-enable the button
                    }
                });
            }
        });
    }

    private void sendNotification(String topic, String title, String body) {
        Notification notification = new Notification(
                new NotificationData(
                        topic,
                        new HashMap<String, String>() {{
                            put("title", title);
                            put("body", body);
                        }}
                )
        );
        Log.d ( "Authorization token","Token received in NotificationAPI "+"Bearer "+accessKey );

        NotificationApi.sendNotification().sendNotification(notification,"Bearer "+accessKey).enqueue(new retrofit2.Callback<Notification>() {
            @Override
            public void onResponse(retrofit2.Call<Notification> call, retrofit2.Response<Notification> response) {
                Toast.makeText(NoticeWindow.this, "Notification Sent successfully"+response.message (), Toast.LENGTH_SHORT).show();
                Log.d ( "Response ","Response Message: "+response.code () );
            }

            @Override
            public void onFailure(retrofit2.Call<Notification> call, Throwable throwable) {
                Toast.makeText(NoticeWindow.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessTokenValue() {
        return AccessToken.getAccessToken();
    }
}
