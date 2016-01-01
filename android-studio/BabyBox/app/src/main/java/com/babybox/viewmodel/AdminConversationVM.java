package com.babybox.viewmodel;

import java.io.Serializable;

public class AdminConversationVM implements Serializable, Comparable<AdminConversationVM> {
    public Long id;
    public Long postId;
    public Long postImage;
    public String postTitle;
    public Long postPrice;
    public boolean postSold;
    public Long user1Id;
    public String user1Name;
    public Long user2Id;
    public String user2Name;
    public Long lastMessageDate;
    public String lastMessage;
    public boolean lastMessageHasImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPostImage() {
        return postImage;
    }

    public void setPostImage(Long postImage) {
        this.postImage = postImage;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public Long getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(Long postPrice) {
        this.postPrice = postPrice;
    }

    public boolean isPostSold() {
        return postSold;
    }

    public void setPostSold(boolean postSold) {
        this.postSold = postSold;
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser1Name() {
        return user1Name;
    }

    public void setUser1Name(String user1Name) {
        this.user1Name = user1Name;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }

    public String getUser2Name() {
        return user2Name;
    }

    public void setUser2Name(String user2Name) {
        this.user2Name = user2Name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isLastMessageHasImage() {
        return lastMessageHasImage;
    }

    public void setLastMessageHasImage(boolean lastMessageHasImage) {
        this.lastMessageHasImage = lastMessageHasImage;
    }

    public Long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    @Override
    public int compareTo(AdminConversationVM o) {
        return this.getLastMessageDate().compareTo(o.getLastMessageDate());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof AdminConversationVM))
            return false;

        AdminConversationVM o = (AdminConversationVM) other;
        return this.id.equals(o.id);
    }
}
