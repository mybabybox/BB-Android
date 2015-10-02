package com.babybox.viewmodel;

public class NewCommentVM {
    public Long postId;
    public String body;
    boolean android;

    public NewCommentVM(Long postId, String body) {
        this.postId = postId;
        this.body = body;
        this.android = true;
    }
}
