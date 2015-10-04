package com.babybox.viewmodel;

import com.babybox.app.AppController;

public class NewPostVM {
    public Long catId;
    public String title, body;
    public double price;
    public String deviceType;

    public NewPostVM(Long catId, String title, String body, double price) {
        this.catId = catId;
        this.title = title;
        this.body = body;
        this.price = price;
        this.deviceType = AppController.DeviceType.ANDROID.name();
    }
}
