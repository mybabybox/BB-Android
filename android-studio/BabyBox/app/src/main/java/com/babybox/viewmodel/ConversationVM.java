package com.babybox.viewmodel;

import java.io.Serializable;

public class ConversationVM implements Serializable, Comparable<ConversationVM> {
    public Long id;
    public Long postId;
    public Long postImage;
    public String postTitle;
    public double postPrice;
    public boolean postOwner;
    public boolean postSold;
    public Long userId;
    public String userName;
    public Long lastMessageDate;
    public String lastMessage;
    public boolean lastMessageHasImage;
    public Long unread = 0L;
    public ConversationOrderVM order;

    // Seller
    public String note;
    public String orderTransactionState;
    public String highlightColor;

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

    public double getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(double postPrice) {
        this.postPrice = postPrice;
    }

    public boolean isPostOwner() {
        return postOwner;
    }

    public void setPostOwner(boolean postOwner) {
        this.postOwner = postOwner;
    }

    public boolean isPostSold() {
        return postSold;
    }

    public void setPostSold(boolean postSold) {
        this.postSold = postSold;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Long getUnread() {
        return unread;
    }

    public void setUnread(Long unread) {
        this.unread = unread;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderTransactionState() {
        return orderTransactionState;
    }

    public void setOrderTransactionState(String orderTransactionState) {
        this.orderTransactionState = orderTransactionState;
    }

    public String getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    public ConversationOrderVM getOrder() {
        return order;
    }

    public void setOrder(ConversationOrderVM order) {
        this.order = order;
    }

    @Override
    public int compareTo(ConversationVM o) {
        return this.getLastMessageDate().compareTo(o.getLastMessageDate());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof ConversationVM))
            return false;

        ConversationVM o = (ConversationVM) other;
        return this.id.equals(o.id);
    }
}
