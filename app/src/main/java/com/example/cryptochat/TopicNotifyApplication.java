package com.example.cryptochat;

import android.app.Application;

public class TopicNotifyApplication extends Application {


    static ChatActivity chatActivity = null;

    public static ChatActivity getChatActivity() {
        return chatActivity;
    }

    public static void setChatActivity(ChatActivity chatActivity) {
        TopicNotifyApplication.chatActivity = chatActivity;
    }
}
