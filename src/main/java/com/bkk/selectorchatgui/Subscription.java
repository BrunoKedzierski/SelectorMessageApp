package com.bkk.selectorchatgui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Subscription implements Serializable {
    private static final AtomicInteger ID = new AtomicInteger(0);
    String subscriptionId;
    Set<String> subscribedTo;
    List<Message> messages;
    boolean authorized;

    public Subscription() {
        subscriptionId = String.valueOf( ID.incrementAndGet());
        this.subscribedTo = new HashSet<>();
        this.messages = new ArrayList<>();
        authorized= false;
    }

    public Set<String> getSubscribedTo() {
        return subscribedTo;
    }

    public void setSubscribedTo(Set<String> subscribedTo) {
        this.subscribedTo = subscribedTo;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void  addSubscribedTo(String topic){
        subscribedTo.add(topic);
    }
    public boolean removeSubscribedTo(String topic){
        return subscribedTo.remove(topic);
    }
    public void  addmessage(String content, String topic, String sender){

        messages.add(new Message(topic, sender, content));

    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean initialized) {
        this.authorized = initialized;
    }
}
