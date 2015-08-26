package com.babybox.viewmodel;

public class CategoryVM {
    public Long id;
    public String image;
    public String name;
    public String desc;
    public Boolean system;
    public String type;
    public int seq;

    // TEMP - for api testing
    public CategoryVM(CommunitiesWidgetChildVM comm) {
        this.id = comm.id;
        this.image = comm.gi;
        this.name = comm.dn;
        this.desc = comm.msg;
        this.system = comm.sys;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean isSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
