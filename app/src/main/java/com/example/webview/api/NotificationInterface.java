package com.example.webview.api;

import com.example.webview.model.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationInterface {
    @POST("/v1/projects/your-project-id/messages:send")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    }
    )
    Call<Notification> sendNotification(
            @Body Notification message,
            @Header("Authorization") String accessToken
    ) ;

}
