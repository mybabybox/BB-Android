package com.babybox.viewmodel;

public class UserVM extends UserVMLite {
    public String aboutMe;
    public LocationVM location;
    public SettingVM setting;

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public LocationVM getLocation() {
        return location;
    }

    public void setLocation(LocationVM location) {
        this.location = location;
    }

    public SettingVM getSetting() {
        return setting;
    }

    public void setSetting(SettingVM setting) {
        this.setting = setting;
    }
}

