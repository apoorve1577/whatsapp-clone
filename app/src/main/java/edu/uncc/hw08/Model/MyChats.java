package edu.uncc.hw08.Model;

import java.util.ArrayList;
import java.util.Map;

public class MyChats {

    public MyChats(){}

    String name;
    String textChat;

    public MyChats(String name, String textChat, String timeStamp, String userId) {
        this.name = name;
        this.textChat = textChat;
        this.timeStamp = timeStamp;
        this.userId = userId;
    }

    String timeStamp;
    String userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextChat() {
        return textChat;
    }

    public void setTextChat(String textChat) {
        this.textChat = textChat;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
