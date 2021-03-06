package com.babybox.viewmodel;

import java.io.Serializable;

public class ActivityVM implements Serializable {
    public Long id;
    public long createdDate;
    public String activityType;
    public Boolean userIsOwner;
    public Long actor;
    public Long actorImage;
    public String actorName;
    public String actorType;
    public Long target;
    public Long targetImage;
    public String targetName;
    public String targetType;
    public Boolean viewed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Boolean getUserIsOwner() {
        return userIsOwner;
    }

    public void setUserIsOwner(Boolean userIsOwner) {
        this.userIsOwner = userIsOwner;
    }

    public Long getActor() {
        return actor;
    }

    public void setActor(Long actor) {
        this.actor = actor;
    }

    public Long getActorImage() {
        return actorImage;
    }

    public void setActorImage(Long actorImage) {
        this.actorImage = actorImage;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public Long getTargetImage() {
        return targetImage;
    }

    public void setTargetImage(Long targetImage) {
        this.targetImage = targetImage;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Boolean isViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }
}
