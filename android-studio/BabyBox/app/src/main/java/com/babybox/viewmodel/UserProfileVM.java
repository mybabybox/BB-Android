package com.babybox.viewmodel;

public class UserProfileVM {
    public String name;
    public String desc;
    public LocationVM location;
    public long createdDate;
    public Long numFollowing;
    public Long numFollower;
    public Long numPosts;
    public Long numCollections;
    public Long numLikes;
    public boolean fbLogin = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public LocationVM getLocation() {
        return location;
    }

    public void setLocation(LocationVM location) {
        this.location = location;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getNumFollowing() {
        return numFollowing;
    }

    public void setNumFollowing(Long numFollowing) {
        this.numFollowing = numFollowing;
    }

    public Long getNumFollower() {
        return numFollower;
    }

    public void setNumFollower(Long numFollower) {
        this.numFollower = numFollower;
    }

    public Long getNumPosts() {
        return numPosts;
    }

    public void setNumPosts(Long numPosts) {
        this.numPosts = numPosts;
    }

    public Long getNumCollections() {
        return numCollections;
    }

    public void setNumCollections(Long numCollections) {
        this.numCollections = numCollections;
    }

    public Long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Long numLikes) {
        this.numLikes = numLikes;
    }

    public boolean isFbLogin() {
        return fbLogin;
    }

    public void setFbLogin(boolean fbLogin) {
        this.fbLogin = fbLogin;
    }
}
