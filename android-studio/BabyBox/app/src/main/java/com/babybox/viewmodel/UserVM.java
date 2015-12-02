package com.babybox.viewmodel;

import org.joda.time.DateTime;

public class UserVM extends UserVMLite {
    public String email;
    public String aboutMe;
    public String firstName;
    public String lastName;
    public String gender;
    public String birthYear;
    public LocationVM location;
    public SettingVM setting;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
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

    @Override
    public String toString() {
        return "id=" + id + "\n" +
                "name=" + lastName + " " + firstName + "\n" +
                "email=" + email + "\n" +
                "emailValidated=" + emailValidated + "\n" +
                "fbLogin=" + isFbLogin + "\n" +
                "signupDate=" + new DateTime(createdDate) + "\n" +
                "lastLogin=" + new DateTime(lastLogin) + "\n" +
                "totalLogin=" + totalLogin + "\n" +
                "numLikes=" + numLikes + "\n" +
                "numFollowers=" + numFollowers + "\n" +
                "numFollowings=" + numFollowings + "\n" +
                "numProducts=" + numProducts;
    }
}

