package com.babybox.viewmodel;

import java.util.List;

public class SellerVM extends UserVMLite {
    public String aboutMe;
    public List<PostVMLite> posts;
    public Long numMoreProducts = 0L;

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public List<PostVMLite> getPosts() {
        return posts;
    }

    public void setPosts(List<PostVMLite> posts) {
        this.posts = posts;
    }

    public Long getNumMoreProducts() {
        return numMoreProducts;
    }

    public void setNumMoreProducts(Long numMoreProducts) {
        this.numMoreProducts = numMoreProducts;
    }
}

