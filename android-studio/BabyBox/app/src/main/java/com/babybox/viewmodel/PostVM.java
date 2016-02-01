package com.babybox.viewmodel;

import java.util.List;

public class PostVM extends PostVMLite {
    public Long ownerNumProducts;
    public Long ownerNumFollowers;
    public Long ownerLastLogin;
    public Long createdDate;
    public Long updatedDate;
    public String body;
    public String categoryType;
    public String categoryName;
    public String categoryIcon;
    public Long categoryId;
    public List<CommentVM> latestComments;
    public List<PostVMLite> relatedPosts;

    public boolean isOwner = false;
    public boolean isFollowingOwner = false;

    public String deviceType;

    public Long getOwnerNumProducts() {
        return ownerNumProducts;
    }

    public void setOwnerNumProducts(Long ownerNumProducts) {
        this.ownerNumProducts = ownerNumProducts;
    }

    public Long getOwnerNumFollowers() {
        return ownerNumFollowers;
    }

    public void setOwnerNumFollowers(Long ownerNumFollowers) {
        this.ownerNumFollowers = ownerNumFollowers;
    }

    public Long getOwnerLastLogin() {
        return ownerLastLogin;
    }

    public void setOwnerLastLogin(Long ownerLastLogin) {
        this.ownerLastLogin = ownerLastLogin;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<CommentVM> getLatestComments() {
        return latestComments;
    }

    public void setLatestComments(List<CommentVM> latestComments) {
        this.latestComments = latestComments;
    }

    public List<PostVMLite> getRelatedPosts() {
        return relatedPosts;
    }

    public void setRelatedPosts(List<PostVMLite> relatedPosts) {
        this.relatedPosts = relatedPosts;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public boolean isFollowingOwner() {
        return isFollowingOwner;
    }

    public void setFollowingOwner(boolean isFollowingOwner) {
        this.isFollowingOwner = isFollowingOwner;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}