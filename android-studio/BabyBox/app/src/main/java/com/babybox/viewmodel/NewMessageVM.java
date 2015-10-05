package com.babybox.viewmodel;

import com.babybox.app.AppController;

public class NewMessageVM {
    public Long conversationId;
    public String body;
    public boolean withPhotos;
    public String deviceType;

    public NewMessageVM(Long conversationId, String body, boolean withPhotos) {
        this.conversationId = conversationId;
        this.body = body;
        this.withPhotos = withPhotos;
        this.deviceType = AppController.DeviceType.ANDROID.name();
    }
}
