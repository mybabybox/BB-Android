package com.babybox.viewmodel;

public class UserVM extends UserVMLite {
    public String aboutMe;
    public LocationVM location;
    public SettingsVM settings;

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

    public SettingsVM getSettings() {
        return settings;
    }

    public void setSettings(SettingsVM settings) {
        this.settings = settings;
    }
}

