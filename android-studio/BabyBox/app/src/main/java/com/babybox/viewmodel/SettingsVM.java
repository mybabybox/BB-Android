package com.babybox.viewmodel;

public class SettingsVM {
    public long id;
    public boolean emailNewPost;
    public boolean emailNewConversation;
    public boolean emailNewComment;
    public boolean emailNewPromotions;
    public boolean pushNewConversation;
    public boolean pushNewComment;
    public boolean pushNewFollow;
    public boolean pushNewFeedback;
    public boolean pushNewPromotions;
    public String systemAndroidVersion;
    public String systemIosVersion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isEmailNewPost() {
        return emailNewPost;
    }

    public void setEmailNewPost(boolean emailNewPost) {
        this.emailNewPost = emailNewPost;
    }

    public boolean isEmailNewConversation() {
        return emailNewConversation;
    }

    public void setEmailNewConversation(boolean emailNewConversation) {
        this.emailNewConversation = emailNewConversation;
    }

    public boolean isEmailNewComment() {
        return emailNewComment;
    }

    public void setEmailNewComment(boolean emailNewComment) {
        this.emailNewComment = emailNewComment;
    }

    public boolean isEmailNewPromotions() {
        return emailNewPromotions;
    }

    public void setEmailNewPromotions(boolean emailNewPromotions) {
        this.emailNewPromotions = emailNewPromotions;
    }

    public boolean isPushNewConversation() {
        return pushNewConversation;
    }

    public void setPushNewConversation(boolean pushNewConversation) {
        this.pushNewConversation = pushNewConversation;
    }

    public boolean isPushNewComment() {
        return pushNewComment;
    }

    public void setPushNewComment(boolean pushNewComment) {
        this.pushNewComment = pushNewComment;
    }

    public boolean isPushNewFollow() {
        return pushNewFollow;
    }

    public void setPushNewFollow(boolean pushNewFollow) {
        this.pushNewFollow = pushNewFollow;
    }

    public boolean isPushNewFeedback() {
        return pushNewFeedback;
    }

    public void setPushNewFeedback(boolean pushNewFeedback) {
        this.pushNewFeedback = pushNewFeedback;
    }

    public boolean isPushNewPromotions() {
        return pushNewPromotions;
    }

    public void setPushNewPromotions(boolean pushNewPromotions) {
        this.pushNewPromotions = pushNewPromotions;
    }

    public String getSystemAndroidVersion() {
        return systemAndroidVersion;
    }

    public void setSystemAndroidVersion(String systemAndroidVersion) {
        this.systemAndroidVersion = systemAndroidVersion;
    }

    public String getSystemIosVersion() {
        return systemIosVersion;
    }

    public void setSystemIosVersion(String systemIosVersion) {
        this.systemIosVersion = systemIosVersion;
    }
}

