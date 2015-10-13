package com.babybox.viewmodel;

import java.io.Serializable;

public class CollectionVM implements Serializable {
    public Long id;
    public String name;
    public String desc;
    public Boolean system;
    public String type;
    public int seq;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
