package com.babybox.viewmodel;

public class NewMessageVM {
    public Long conversationId;
    public Long receiverId;
    public String msgText;
    public boolean withPhotos;
    public boolean android;

    public NewMessageVM(Long conversationId, Long receiverId, String msgText, boolean withPhotos) {
        this.conversationId = conversationId;
        this.receiverId = receiverId;
        this.msgText = msgText;
        this.withPhotos = withPhotos;
        this.android = true;
    }
}
