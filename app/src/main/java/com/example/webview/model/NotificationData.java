package com.example.webview.model;

import java.util.HashMap;

public class NotificationData {
    public String getTopic() {
        return topic;
    }

    public NotificationData(String topic, HashMap<String, String> notifiction) {
        this.topic = topic;
        this.notifiction = notifiction;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public HashMap<String, String> getNotifiction() {
        return notifiction;
    }

    public void setNotifiction(HashMap<String, String> notifiction) {
        this.notifiction = notifiction;
    }

    String topic;
    HashMap<String,String> notifiction;
}
