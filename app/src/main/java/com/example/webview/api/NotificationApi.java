package com.example.webview.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationApi {
    static Retrofit retrofit=null;
    public static NotificationInterface sendNotification(){
        if(retrofit==null){
            retrofit= new Retrofit.Builder ().baseUrl("https://fcm.googleapis.com").addConverterFactory( GsonConverterFactory.create () ).build();
        }
        return retrofit.create ( NotificationInterface.class );
    }
}
