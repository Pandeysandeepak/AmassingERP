package com.example.webview.rest;

import static com.example.webview.MainActivity.accessKey;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMNotificationSender {

    private static final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/your-project-id/messages:send";

    public static void sendNotification(String title, String body, String channelName, Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            // Create notification JSON object
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            // Create message JSON object
            JSONObject message = new JSONObject();
            message.put("topic", channelName);
            message.put("notification", notification);

            // Create main JSON object
            JSONObject json = new JSONObject();
            json.put("message", message);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    FCM_API_URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response Message: ", "Responded Successfully");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d("Error Message: ", "Something Went Wrong: " + volleyError.getMessage());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json; charset=utf-8");
                    params.put("Authorization", "Bearer " + accessKey);
                    return params;
                }
            };

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
