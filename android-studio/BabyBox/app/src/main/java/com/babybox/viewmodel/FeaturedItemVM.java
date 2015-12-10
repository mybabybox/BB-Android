package com.babybox.viewmodel;

public class FeaturedItemVM {
    public Long id;
    public Long createdDate;
    public String itemType;
    public String name;
    public String description;
    public String image;
    public int seq;

    public String destinationType;
    public Long destinationObjId;
    public String destinationObjName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public Long getDestinationObjId() {
        return destinationObjId;
    }

    public void setDestinationObjId(Long destinationObjId) {
        this.destinationObjId = destinationObjId;
    }

    public String getDestinationObjName() {
        return destinationObjName;
    }

    public void setDestinationObjName(String destinationObjName) {
        this.destinationObjName = destinationObjName;
    }

    @Override
    public String toString() {
        return "id=" + id + "\n" +
                "itemType=" + itemType + "\n" +
                "name=" + name + "\n" +
                "seq=" + seq + "\n" +
                "destinationType=" + destinationType + "\n" +
                "destinationObjId=" + destinationObjId + "\n" +
                "destinationObjName=" + destinationObjName;
    }
}

