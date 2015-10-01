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


    public boolean isFollowdByUser = false;
    public boolean and = false;
    public boolean ios = false;
    public boolean mob = false;

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
        this.type = post.type;
        this.categoryType = post.ctyp;
        this.categoryName = post.cn;
        this.categoryIcon = post.ci;
        this.categoryId = post.cid;
        this.numViews = post.nov;
        this.numLikes = post.nol;
        this.isOwner = post.isO;
        this.isLiked = post.isLike;
        this.and = post.and;
        this.ios = post.ios;
        this.mob = post.mob;
        this.price = ViewUtil.random(1, 500);
    }

    public boolean isFollowdByUser() {
        return isFollowdByUser;
    }

    public void setFollowdByUser(boolean isFollowdByUser) {
        this.isFollowdByUser = isFollowdByUser;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return and;
    }

    public void setAndroid(boolean and) {
        this.and = and;
    }

    public boolean isIOS() {
        return ios;
    }

    public void setIOS(boolean ios) {
        this.ios = ios;
    }

    public boolean isMobile() {
        return mob;
    }

    public void setMobile(boolean mob) {
        this.mob = mob;
    }
}