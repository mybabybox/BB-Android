package com.babybox.viewmodel;

import com.babybox.mock.CommunityPostVM;
import com.babybox.util.ViewUtil;

public class PostVM extends PostVMLite {
    public Long ownerId;
    public String postedBy;
    public long createdDate;
    public long updatedDate;
    public String desc;
    public String categoryType;
    public String categoryName;
    public String categoryIcon;
    public Long categoryId;
    public boolean isFollowingOwner = false;

    public boolean mobile = false;
    public boolean android = false;
    public boolean ios = false;

    public PostVM(CommunityPostVM post) {
        this.id = post.id;
        this.ownerId = post.oid;
        this.postedBy = post.p;
        this.createdDate = post.t;
        this.updatedDate = post.ut;
        this.title = post.ptl;
        this.desc = post.pt;
        this.numComments = post.n_c;
        this.images = post.imgs;
        this.postType = post.type;
        this.categoryType = post.ctyp;
        this.categoryName = post.cn;
        this.categoryIcon = post.ci;
        this.categoryId = post.cid;
        this.numViews = post.nov;
        this.numLikes = post.nol;
        this.isOwner = post.isO;
        this.isLiked = post.isLike;
        this.mobile = post.mob;
        this.android = post.and;
        this.ios = post.ios;
        this.price = ViewUtil.random(1, 500);
    }

    public boolean isFollowingOwner() {
        return isFollowingOwner;
    }

    public void setFollowingOwner(boolean isFollowingOwner) {
        this.isFollowingOwner = isFollowingOwner;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public boolean isAndroid() {
        return android;
    }

    public void setAndroid(boolean android) {
        this.android = android;
    }

    public boolean isIOS() {
        return ios;
    }

    public void setIOS(boolean ios) {
        this.ios = ios;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }
}