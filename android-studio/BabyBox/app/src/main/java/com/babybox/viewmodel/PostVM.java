package com.babybox.viewmodel;

import java.util.List;

public class PostVM extends PostVMLite {
    public Long ownerId;
    public String ownerName;
    public Long ownerNumFollowers;
    public long createdDate;
    public long updatedDate;
    public String body;
    public String categoryType;
    public String categoryName;
    public String categoryIcon;
    public Long categoryId;
    public List<CommentVM> comments;

    public boolean isOwner = false;
    public boolean isFollowingOwner = false;

    public String deviceType;

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getOwnerNumFollowers() {
        return ownerNumFollowers;
    }

    public void setOwnerNumFollowers(Long ownerNumFollowers) {
        this.ownerNumFollowers = ownerNumFollowers;
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

    public List<CommentVM> getComments() {
        return comments;
    }

    public void setComments(List<CommentVM> comments) {
        this.comments = comments;
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