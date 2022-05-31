package com.bkk.selectorchatgui;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Message implements Serializable {
    private static final AtomicInteger ID = new AtomicInteger(0);
    private static final Gson gson = new Gson();
    String messageId;
    private String topic;
    private String sender;
    private String content;

    public Message(String topic, String sender, String content) {
        this.topic  = topic;
        this.sender = sender;
        this.content = content;
        messageId = String.valueOf( ID.incrementAndGet());

    }

    public Message(String messageId, String topic, String sender, String content) {
        this.messageId = messageId;
        this.topic = topic;
        this.sender = sender;
        this.content = content;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return
                "*******************************\n"  +
                "topic:" + topic + '\n' +
                "sender:" + sender + '\n' +
                "content:" + content + '\n';
    }
}
