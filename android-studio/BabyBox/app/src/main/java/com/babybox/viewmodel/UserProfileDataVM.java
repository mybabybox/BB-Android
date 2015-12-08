package com.babybox.viewmodel;


public class UserProfileDataVM {

    public String parent_email;
    public String parent_aboutme;
    public String parent_displayname;
    public String parent_firstname;
    public String parent_lastname;
    public int parent_location;

    public String getParent_email() {
        return parent_email;
    }

    public void setParent_email(String parent_email) {
        this.parent_email = parent_email;
    }

    public int getParent_location() {
        return parent_location;
    }

    public void setParent_location(int parent_location) {
        this.parent_location = parent_location;
    }

    public String getParent_aboutme() {
        return parent_aboutme;
    }

    public void setParent_aboutme(String parent_aboutme) {
        this.parent_aboutme = parent_aboutme;
    }

    public String getParent_displayname() {
        return parent_displayname;
    }

    public void setParent_displayname(String parent_displayname) {
        this.parent_displayname = parent_displayname;
    }

    public String getParent_firstname() {
        return parent_firstname;
    }

    public void setParent_firstname(String parent_firstname) {
        this.parent_firstname = parent_firstname;
    }

    public String getParent_lastname() {
        return parent_lastname;
    }

    public void setParent_lastname(String parent_lastname) {
        this.parent_lastname = parent_lastname;
    }
}

