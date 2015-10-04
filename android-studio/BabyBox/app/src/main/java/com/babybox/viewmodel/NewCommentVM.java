package com.babybox.viewmodel;

import com.babybox.app.AppController;

public class NewCommentVM {
    public Long postId;
    public String body;
    public String deviceType;

    public NewCommentVM(Long postId, String body) {
        this.postId = postId;
        this.body = body;
        this.deviceType = AppController.DeviceType.ANDROID.name();
    }
}
