package com.babybox.viewmodel;

public class GameBadgeVM {
    public Long id;
    public String badgeType;
    public String name;
    public String description;
    public String icon;
    public int seq;
    public Boolean awarded;
    public Long awardedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Boolean getAwarded() {
        return awarded;
    }

    public void setAwarded(Boolean awarded) {
        this.awarded = awarded;
    }

    public Long getAwardedDate() {
        return awardedDate;
    }

    public void setAwardedDate(Long awardedDate) {
        this.awardedDate = awardedDate;
    }

    @Override
    public String toString() {
        return "id=" + id + "\n" +
                "badgeType=" + badgeType + "\n" +
                "name=" + name + "\n" +
                "seq=" + seq + "\n" +
                "awarded=" + awarded;
    }
}

