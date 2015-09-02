package com.babybox.viewmodel;

public class NewCommentVM {
    public Long postId;
    public String desc;
    boolean withPhotos;
    boolean android;

    public NewCommentVM(Long postId, String desc, boolean withPhotos) {
        this.postId = postId;
        this.desc = desc;
        this.withPhotos = withPhotos;
        this.android = true;
    }

}
