package org.example.entities;

import java.io.Serializable;

public class Message implements Serializable{
    private String text;
    private String senderNickname;

    public Message() {
    }

    public Message(String text, String senderNickname) {
        this.text = text;
        this.senderNickname = senderNickname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    @Override
    public String toString() {
        return senderNickname + ":" + text;
    }

}
