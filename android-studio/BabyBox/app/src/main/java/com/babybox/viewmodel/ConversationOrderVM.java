package com.babybox.viewmodel;

import java.io.Serializable;

public class ConversationOrderVM implements Serializable, Comparable<ConversationOrderVM> {
    public Long id;
    public Long conversationId;
    public Long userId;
    public String userName;
    public Long createdDate;
    public Long updatedDate;
    public boolean cancelled;
    public Long cancelDate;
    public boolean accepted;
    public Long acceptDate;
    public boolean declined;
    public Long declineDate;
    public boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
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

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Long getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Long cancelDate) {
        this.cancelDate = cancelDate;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Long getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(Long acceptDate) {
        this.acceptDate = acceptDate;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public Long getDeclineDate() {
        return declineDate;
    }

    public void setDeclineDate(Long declineDate) {
        this.declineDate = declineDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int compareTo(ConversationOrderVM o) {
        return this.updatedDate.compareTo(o.updatedDate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof ConversationOrderVM))
            return false;

        ConversationOrderVM o = (ConversationOrderVM) other;
        return this.id.equals(o.id);
    }
}
