package com.babybox.viewmodel;

public class ConversationVM implements Comparable<ConversationVM> {
    public Long id;
    public Long postId;
    public Long postImage;
    public String postTitle;
    public Long postPrice;
    public Long userId;
    public String userName;
    public Long lastMessageDate;
    public String lastMessage;
    public Long unread = 0L;
    public Boolean isRead = false;
    public Boolean isToday;

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

    public Long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getUnread() {
        return unread;
    }

    public void setUnread(Long unread) {
        this.unread = unread;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean isToday() {
        return isToday;
    }

    public void setIsToday(Boolean isToday) {
        this.isToday = isToday;
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
