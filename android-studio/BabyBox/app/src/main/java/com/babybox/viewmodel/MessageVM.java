package com.babybox.viewmodel;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MessageVM implements Serializable, Comparable<MessageVM> {
    public Long id;
    public Long createdDate;
    public Long senderId;
    public String senderName;
    public Long receiverId;
    public String receiverName;
    public String body;
    public boolean system = false;
    public boolean hasImage = false;
    public Long image;

    public MessageVM(JSONObject jsonObj) {
        try {
            this.id = jsonObj.getLong("id");
            this.senderId = jsonObj.getLong("senderId");
            this.senderName = jsonObj.getString("senderName");
            this.receiverId = jsonObj.getLong("receiverId");
            this.receiverName = jsonObj.getString("receiverName");
            this.createdDate = jsonObj.getLong("createdDate");
            this.body = jsonObj.getString("body");
            this.system = jsonObj.getBoolean("system");
            this.image = jsonObj.getLong("image");
            this.hasImage = jsonObj.getBoolean("hasImage");
        } catch (JSONException e) {
            Log.e(this.getClass().getSimpleName(), "Error construct message: exception", e);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Long getImage() {
        return image;
    }

    public void setImage(Long image) {
        this.image = image;
    }

    @Override
    public int compareTo(MessageVM o) {
        return this.getCreatedDate().compareTo(o.getCreatedDate());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof MessageVM))
            return false;

        MessageVM o = (MessageVM) other;
        return this.id.equals(o.id);
    }
}
