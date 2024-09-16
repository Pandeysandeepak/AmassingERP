package com.example.webview;

import static android.content.ContentValues.TAG;
import static com.example.webview.FCMToken.fcm_token;
import static com.example.webview.MainActivity.progressDialog;
import static com.example.webview.NotificationChannelHelper.channelName;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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


public class Broadcast extends Fragment {

    private EditText notificationHead, notificationBody;
    public static String nHeadData;
    public static String nBodyData;
    public static String accessKey;
    private LinearLayout broadcast;
    public   LinearLayout homeBtn, menuBtn, settingsBtn, noticeBtn,bottom_navigation;
    View view;


    private final OkHttpClient okHttpClient = new OkHttpClient();
    private static final String BASE_URL = "https://edschool.in/demo/message/broadcast-action.php";  // This return the message data in JSON format (like Head Body )




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view=inflater.inflate ( R.layout.fragment_broadcast, container, false );
        homeBtn = view.findViewById(R.id.HomeButton);
        menuBtn = view.findViewById(R.id.menuButton);
        settingsBtn = view.findViewById(R.id.SettingsButton);
        noticeBtn = view.findViewById(R.id.NoticeButton);
        FirebaseMessaging.getInstance();


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( (v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                showAlertDialog();
                return true;
            }
            return false;
        } );



//        setupButtonListeners ();

        // Show progress Dialogue while loading Activity
        progressDialog.show ();
        // Simulate loading for 3 seconds
        new Handler ().postDelayed( () -> {
            // Dismiss the progress dialog when loading is done
            progressDialog.dismiss ();
        }, 3000); // Delay for 3 seconds






        notificationHead = view.findViewById ( R.id.NoticeHeading );
        notificationBody = view.findViewById(R.id.NoticeBody);


        broadcast= view.findViewById ( R.id.Broadcast );

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

        broadcast.setOnClickListener(v -> {
            nHeadData = notificationHead.getText().toString().trim();
            nBodyData = notificationBody.getText().toString().trim();

            // Check if both the head and body are not empty or null
            if (!nHeadData.isEmpty() && !nBodyData.isEmpty()) {

                Log.e(TAG, "AccessToken is: " + accessKey);

                // Create and subscribe to notification channel
                NotificationChannelHelper.createNotificationChannel( getContext () );
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

                }
                else if (nBodyData.isEmpty ()) {
                    notificationBody.requestFocus ();
                    notificationBody.setBackgroundResource ( R.drawable.emptyeffect );

                }
            }
        });


        // hide keyboard while pressing enter from the Keyboard

        setupEditTextListener ( notificationBody );
        setupEditTextListener ( notificationHead );

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        return view;
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

        broadcast.setEnabled(false);  // Disable the button to prevent multiple clicks

        call.enqueue(new Callback () {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Toast.makeText(requireContext (), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    broadcast.setEnabled(true);  // Re-enable the button

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                    try {
                        if (response.isSuccessful()) {
                            String resp = Objects.requireNonNull(response.body()).string();
                            Log.d("Response From Server :  ",resp);
                            notificationHead.setText("");
                            notificationBody.setText("");
                        } else {
                            Log.d("Error response","Error in sending notification"+response.code ());
                        }
                    } catch (IOException e) {
                        Toast.makeText(requireContext (), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };

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
                Toast.makeText(requireContext (), "Notification sent Successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Read error stream
                BufferedReader errorReader = new BufferedReader(new InputStreamReader (connection.getErrorStream(), "utf-8"));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
                Toast.makeText ( requireContext (), "Failed to send notification", Toast.LENGTH_SHORT ).show ();
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
        accessKey=AccessToken.getAccessToken ();
        return accessKey;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity ().onBackPressed();
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel() );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setupEditTextListener(EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService( Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true; // Return true if you handled the action
                }
                return false; // Return false to let other listeners handle it
            }
        });
    }




}