package com.babybox.viewmodel;

public class NewPostVM {
    public Long catId;
    public String title, desc;
    public double price;
    boolean withPhotos;
    boolean android;

    public NewPostVM(Long catId, String title, String desc, double price, boolean withPhotos) {
        this.catId = catId;
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.withPhotos = withPhotos;
        this.android = true;
    }
}
