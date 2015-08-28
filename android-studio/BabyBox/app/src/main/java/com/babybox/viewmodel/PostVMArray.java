package com.babybox.viewmodel;

import java.util.List;

/**
 * Created by User on 2/4/15.
 */
public class PostVMArray {
    public List<PostVM> posts;

    public List<PostVM> getPosts() {
        return posts;
    }

    public void setPosts(List<PostVM> posts) {
        this.posts = posts;
    }
}
