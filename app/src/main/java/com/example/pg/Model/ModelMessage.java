package com.example.pg.Model;

import com.google.firebase.database.PropertyName;

public class ModelMessage {
    String message, receiver, sender, timestamp, type;
    boolean isSeen;
    public ModelMessage(){

    }

    public ModelMessage(String message, String receiver, String sender, String timestamp, String type) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @PropertyName("isSeen")
    public boolean isSeen() {
        return isSeen;
    }


    @PropertyName("isSeen")
    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

