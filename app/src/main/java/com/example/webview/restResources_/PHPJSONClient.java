package com.example.webview.restResources_;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PHPJSONClient {

    public static void getData() {
        try {
            // 1. Define the PHP endpoint URL
            String urlString = "https://edschool.in/demo/message/broadcast-action.php";  // Change this to your PHP URL
            URL url = new URL(urlString);

            // 2. Create a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");  // or "POST" if your PHP script uses POST

            // 3. Check the response code (200 OK means success)
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {  // Check if the response is successful
                // 4. Read the JSON response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 5. Print the raw JSON response (optional)
                String jsonResponse = response.toString();
                Log.d ( "JSON RESPONSE FROM THE SERVER ","Raw JSON Response: " + jsonResponse);


            } else {
                System.out.println("Failed to get response from the PHP server.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

