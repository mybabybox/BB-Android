package com.babybox.viewmodel;

public class NewPostVM {
    public Long catId;
    public String title, body;
    public double price;
    public boolean android;

    public NewPostVM(Long catId, String title, String body, double price) {
        this.catId = catId;
        this.title = title;
        this.body = body;
        this.price = price;
        this.android = true;
    }
}
