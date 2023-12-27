package org.entities;

import java.io.Serializable;

public class Message implements Serializable{
    private String text;
    private String senderNickname;

    private java.time.LocalDateTime dateTime;
    public Message() {
    }

    public Message(String text, String senderNickname, java.time.LocalDateTime dateTime) {
        this.text = text;
        this.senderNickname = senderNickname;
        this.dateTime = dateTime;
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
