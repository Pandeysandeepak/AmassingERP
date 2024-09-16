package com.example.webview.restResources_;

import static android.content.ContentValues.TAG;
import static com.example.webview.FCMToken.fcm_token;
import static com.example.webview.MainActivity.webView;
import static com.example.webview.NotificationChannelHelper.channelName;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webview.AccessToken;
import com.example.webview.FCMToken;
import com.example.webview.MainActivity;
import com.example.webview.NotificationChannelHelper;
import com.example.webview.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static String nHeadData;
    public static String nBodyData;
    public static String accessKey;
    private LinearLayout broadcast;
    public   LinearLayout homeBtn, menuBtn, settingsBtn, noticeBtn,bottom_navigation;


    private final OkHttpClient okHttpClient = new OkHttpClient();
    private static final String BASE_URL = "https://edschool.in/demo/message/broadcast-action.php";  // This return the message data in JSON format (like Head Body )
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_notice_window);


        homeBtn = findViewById(R.id.HomeButton);
        menuBtn = findViewById(R.id.menuButton);
        settingsBtn = findViewById(R.id.SettingsButton);
        noticeBtn = findViewById(R.id.NoticeButton);

       setupButtonListeners ();

        // Show progress Dialogue while loading Activity
        MainActivity.progressDialog.show ();
        // Simulate loading for 3 seconds
        new Handler ().postDelayed( () -> {
            // Dismiss the progress dialog when loading is done
            MainActivity.progressDialog.dismiss ();
        }, 3000); // Delay for 3 seconds



        notificationHead = findViewById ( R.id.NoticeHeading );
        notificationBody = findViewById(R.id.NoticeBody);


         broadcast= findViewById ( R.id.Broadcast );

        // Obtain the access token in the background
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(this::getAccessTokenValue);
        executorService.execute( FCMToken::generateFCMToken);
        try {
            // Wait for the access token retrieval to complete
            accessKey = future.get();
            Log.e(TAG, "AccessToken retrieved: " + accessKey);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve access token: " + e.getMessage());
        }

        broadcast.setOnClickListener(v -> {
            nHeadData = notificationHead.getText().toString().trim();
            nBodyData = notificationBody.getText().toString().trim();

            // Check if both the head and body are not empty or null
            if (!nHeadData.isEmpty() && !nBodyData.isEmpty()) {

                Log.e(TAG, "AccessToken is: " + accessKey);

                // Create and subscribe to notification channel
                NotificationChannelHelper.createNotificationChannel(getApplicationContext());
                FirebaseMessaging.getInstance().subscribeToTopic(channelName);

                // Send notification data to the database
                ExecutorService executorServiceMain = Executors.newSingleThreadExecutor();
                executorServiceMain.execute(this::publishNotice);

                // Send notification to FCM server
                sendNotification(channelName, nHeadData, nBodyData);

            } else {
                // Handle error: one or both fields are empty
                if (nHeadData.isEmpty ()) {
                    notificationHead.requestFocus ();
                    notificationHead.setBackgroundResource ( R.drawable.emptyeffect );
                    Toast.makeText ( this, "Enter a valid title", Toast.LENGTH_SHORT ).show ();
                }
                else if (nBodyData.isEmpty ()) {
                    notificationBody.requestFocus ();
                    notificationBody.setBackgroundResource ( R.drawable.emptyeffect );
                    Toast.makeText ( this, "Enter valid content", Toast.LENGTH_SHORT ).show ();
                }
            }
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

        runOnUiThread(() -> broadcast.setEnabled(false));  // Disable the button to prevent multiple clicks

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(NoticeWindow.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    broadcast.setEnabled(true);  // Re-enable the button
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    try {
                        if (response.isSuccessful()) {
                            String resp = Objects.requireNonNull(response.body()).string();
                            Log.d("Response From Server :  ",resp);
                            notificationHead.setText("");
                            notificationBody.setText("");
                        } else {
                            Toast.makeText(NoticeWindow.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(NoticeWindow.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        broadcast.setEnabled(true);  // Re-enable the button
                    }
                });
            }
        });
    }

    public void sendNotification(String topic, String title, String body) {
        // Create the Notification object
        try {
            HttpURLConnection connection = createConnection();
            String jsonPayload = "{\n" +
                    "  \"message\": {\n" +
                    "    \"topic\": \"" + topic + "\",\n" +
                    "    \"notification\": {\n" +
                    "      \"title\": \"" + title + "\",\n" +
                    "      \"body\": \"" + body + "\"\n" +
                    "    },\n" +
                    "    \"data\": {\n" +
                    "      \"notificationId\": \"Notification1\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            // Write the payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Toast.makeText(this, "Notification sent Successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Read error stream
                BufferedReader errorReader = new BufferedReader(new InputStreamReader (connection.getErrorStream(), "utf-8"));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
                Log.e(TAG, "Error Response: " + errorResponse.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static HttpURLConnection createConnection() throws IOException {
        URL url = new URL("https://fcm.googleapis.com" + "/v1/projects/amassingerp/messages:send");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessKey);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        httpURLConnection.setDoOutput(true); // To send request body
        return httpURLConnection;
    }

    private String getAccessTokenValue() {
        accessKey= AccessToken.getAccessToken ();
        return accessKey;
    }

    public void setupButtonListeners() {
        homeBtn.setOnClickListener(v -> {
            startActivity ( new Intent (NoticeWindow.this, MainActivity.class) );
            webView.loadUrl("http://edschool.in/cbsps/admin-dashboard.php");
            updateButtonColors(homeBtn);
        });
        menuBtn.setOnClickListener(v -> {
            startActivity ( new Intent (NoticeWindow.this, MainActivity.class) );
            webView.loadUrl("https://edschool.in/demo/menu.php");
            updateButtonColors(menuBtn);
        });
        settingsBtn.setOnClickListener(v -> {
            startActivity ( new Intent (NoticeWindow.this, MainActivity.class) );
            webView.loadUrl("https://edschool.in/demo/settings/setup.php");
            updateButtonColors(settingsBtn);
        });
        noticeBtn.setOnClickListener(v -> {
            startActivity ( new Intent (NoticeWindow.this,NoticeWindow.class) );
            updateButtonColors(noticeBtn);
        });
    }


    public  void updateButtonColors(LinearLayout activeButton) {
        homeBtn.setBackgroundColor( Color.WHITE);
        menuBtn.setBackgroundColor(Color.WHITE);
        settingsBtn.setBackgroundColor(Color.WHITE);
        noticeBtn.setBackgroundColor(Color.WHITE);
        activeButton.setBackgroundColor(Color.GRAY);
    }
}


