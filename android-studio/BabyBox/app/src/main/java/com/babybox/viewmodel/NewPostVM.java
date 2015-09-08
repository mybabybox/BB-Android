package com.babybox.viewmodel;

public class NewPostVM {
    public Long catId;
    public String title, desc;
    public double price;
    public boolean android;

    public NewPostVM(Long catId, String title, String desc, double price) {
        this.catId = catId;
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.android = true;
    }
}
