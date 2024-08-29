package com.example.webview.rest;

public class Notification {
    private String title;
    private String body;

    // Constructor
    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

class AndroidNotification {
    private String channel_id;

    // Constructor
    public AndroidNotification(String channel_id) {
        this.channel_id = channel_id;
    }

    // Getters and Setters
    public String getChannelId() {
        return channel_id;
    }

    public void setChannelId(String channel_id) {
        this.channel_id = channel_id;
    }
}

class Android {
    private AndroidNotification notification;

    // Constructor
    public Android(AndroidNotification notification) {
        this.notification = notification;
    }

    // Getters and Setters
    public AndroidNotification getNotification() {
        return notification;
    }

    public void setNotification(AndroidNotification notification) {
        this.notification = notification;
    }
}

class Message {
    private String topic;  // Changed from token to topic
    private Notification notification;
    private Android android;

    // Constructor
    public Message(String topic, Notification notification, Android android) {
        this.topic = topic;
        this.notification = notification;
        this.android = android;
    }

    // Getters and Setters
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Android getAndroid() {
        return android;
    }

    public void setAndroid(Android android) {
        this.android = android;
    }
}

class NotificationPayload {
    private Message message;

    // Constructor
    public NotificationPayload(Message message) {
        this.message = message;
    }

    // Getters and Setters
    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
