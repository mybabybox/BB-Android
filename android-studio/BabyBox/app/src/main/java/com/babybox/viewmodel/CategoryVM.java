package com.babybox.viewmodel;

import com.babybox.mock.CommunitiesWidgetChildVM;
import com.babybox.mock.CommunityVM;

public class CategoryVM {
    public Long id;
    public String icon;
    public String name;
    public String description;
    public String categoryType;
    public int seq;

    // TEMP - for api testing
    public CategoryVM(CommunitiesWidgetChildVM comm) {
        this.id = comm.id;
        this.icon = comm.gi;
        this.name = comm.dn;
        this.description = comm.msg;
        this.categoryType = comm.tp;
    }

    // TEMP - for api testing
    public CategoryVM(CommunityVM comm) {
        this.id = comm.id;
        this.icon = comm.icon;
        this.name = comm.n;
        this.description = comm.d;
        this.categoryType = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
