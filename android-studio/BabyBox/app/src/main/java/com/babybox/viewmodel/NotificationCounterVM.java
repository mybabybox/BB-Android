package com.babybox.viewmodel;

import java.io.Serializable;

public class NotificationCounterVM implements Serializable {
    public Long id;
    public Long userId;
    public Long activitiesCount;
    public Long conversationsCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActivitiesCount() {
        return activitiesCount;
    }

    public void setActivitiesCount(Long activitiesCount) {
        this.activitiesCount = activitiesCount;
    }

    public Long getConversationsCount() {
        return conversationsCount;
    }

    public void setConversationsCount(Long conversationsCount) {
        this.conversationsCount = conversationsCount;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other == this)
            return true;

        if (!(other instanceof NotificationCounterVM))
            return false;

        NotificationCounterVM o = (NotificationCounterVM) other;
        return this.userId.equals(o.userId);
    }
}
