package com.babybox.viewmodel;


public class CommentVM {
    public Long id;
    public Long ownerId;
    public String postedBy;
    public Long createdDate;
    public boolean hasImage = false;
    public Long[] images;
    public String desc;
    public int seq;

    public boolean isOwner = false;
    public boolean isLike = false;     // filled outside

    public boolean and = false;
    public boolean ios = false;
    public boolean mob = false;

    // helper state
    public boolean imageLoaded = false;

    public CommentVM(CommunityPostCommentVM comment) {
        this.id = comment.id;
        this.ownerId = comment.oid;
        this.postedBy = comment.on;
        this.createdDate = comment.cd;
        this.hasImage = comment.hasImage;
        this.images = comment.imgs;
        this.desc = comment.d;
        this.seq = comment.n;
        this.isOwner = comment.isO;
        this.isLike = comment.isLike;
        this.and = comment.and;
        this.ios = comment.ios;
        this.mob = comment.mob;

        this.imageLoaded = comment.imageLoaded;
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

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Long[] getImages() {
        return images;
    }

    public void setImages(Long[] images) {
        this.images = images;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public boolean isAndroid() {
        return and;
    }

    public void setAndroid(boolean and) {
        this.and = and;
    }

    public boolean isIos() {
        return ios;
    }

    public void setIos(boolean ios) {
        this.ios = ios;
    }

    public boolean isMobile() {
        return mob;
    }

    public void setMobile(boolean mob) {
        this.mob = mob;
    }
}