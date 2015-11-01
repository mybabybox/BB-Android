package com.babybox.viewmodel;

import java.io.Serializable;

public class PostVMLite implements Serializable {
    public Long id;
    public Long ownerId;
    public String ownerName;
    public String title;
    public double price;
    public boolean sold;
    public String postType;
    public String conditionType;
    public Long[] images;
    public Boolean hasImage = false;
    public int numLikes;
    public int numChats;
    public int numBuys;
    public int numComments;
    public int numViews;
    public boolean isLiked = false;
    public long offset;

    // admin fields
    public long baseScore = 0L;
    public double timeScore = 0D;

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public int getNumChats() {
        return numChats;
    }

    public void setNumChats(int numChats) {
        this.numChats = numChats;
    }

    public int getNumBuys() {
        return numBuys;
    }

    public void setNumBuys(int numBuys) {
        this.numBuys = numBuys;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public Long[] getImages() {
        return images;
    }

    public void setImages(Long[] imgs) {
        this.images = imgs;
    }

    public Boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public int getNumViews() {
        return numViews;
    }

    public void setNumViews(int numViews) {
        this.numViews = numViews;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public long getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(long baseScore) {
        this.baseScore = baseScore;
    }

    public double getTimeScore() {
        return timeScore;
    }

    public void setTimeScore(double timeScore) {
        this.timeScore = timeScore;
    }
}