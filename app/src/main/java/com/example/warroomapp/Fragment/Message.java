package com.example.warroomapp.Fragment;

public class Message {
    private String text;
    private String sender; // Indicates whether the message is from the user (self) or the sender

    private String time;
    public Message(String text, String sender, String time) {
        this.text = text;
        this.sender = sender;
        this.time = time;
    }

    public String getText() {
        return text;
    }
    public String getSender() {
        return sender;
    }
    public String getTime() {
        return time;
    }
}
