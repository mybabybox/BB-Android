package com.babybox.viewmodel;

import com.babybox.mock.CommunityPostCommentVM;
import com.babybox.mock.CommunityPostVM;
import com.babybox.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class PostVM {
    public Long id;
    public Long ownerId;
    public String postedBy;
    public long createdDate;
    public long updatedDate;
    public String title;
    public String desc;
    public double price;
    public boolean sold;
    public boolean hasImage = false;
    public int numComments;
    public List<CommentVM> comments;
    public Long[] images;
    public String type;
    public String subType;
    public String categoryType;
    public String categoryName;
    public String categoryIcon;
    public Long categoryId;
    public int numViews;
    public int numLikes;
    public boolean isOwner = false;
    public boolean showMore = false;
    public boolean isCommentable = false;
    public boolean isLiked = false;

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
        this.hasImage = post.hasImage;
        this.numComments = post.n_c;
        this.images = post.imgs;
        this.type = post.type;
        this.subType = post.subtype;
        this.categoryType = post.ctyp;
        this.categoryName = post.cn;
        this.categoryIcon = post.ci;
        this.categoryId = post.cid;
        this.numViews = post.nov;
        this.numLikes = post.nol;
        this.isOwner = post.isO;
        this.showMore = post.showM;
        this.isCommentable = post.isC;
        this.isLiked = post.isLike;
        this.and = post.and;
        this.ios = post.ios;
        this.mob = post.mob;

        this.price = ViewUtil.random(1, 500);

        if (post.cs != null) {
            this.comments = new ArrayList<>();
            for (CommunityPostCommentVM comment : post.cs) {
                this.comments.add(new CommentVM(comment));
            }
        }
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public List<CommentVM> getComments() {
        return comments;
    }

    public void setComments(List<CommentVM> comments) {
        this.comments = comments;
    }

    public Long[] getImages() {
        return images;
    }

    public void setImages(Long[] imgs) {
        this.images = imgs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
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

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    public boolean isCommentable() {
        return isCommentable;
    }

    public void setIsCommentable(boolean isCommentable) {
        this.isCommentable = isCommentable;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
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