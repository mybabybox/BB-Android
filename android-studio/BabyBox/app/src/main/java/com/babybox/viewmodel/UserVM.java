package com.babybox.viewmodel;

import com.babybox.mock.ProfileVM;

public class UserVM extends UserVMLite {
    public long createdDate;
    public String email;
    public String aboutMe;
    public String firstName;
    public String lastName;
    public String gender;
    public String birthYear;
    public LocationVM location;

    // admin readyonly fields
    public String lastLogin;
    public Long totalLogin;
    public boolean isLoggedIn = false;
    public boolean isFbLogin = false;
    public boolean emailValidated = false;
    public boolean newUser = false;
    public boolean isAdmin = false;

    // obsolete
    public boolean isSA = false;
    public boolean isBA = false;
    public boolean isCA = false;
    public boolean isE = false;
    public Long questionsCount;
    public Long answersCount;
    public boolean enableSignInForToday = false;

    public UserVM(ProfileVM profile) {
        this.id = profile.id;
        this.displayName = profile.dn;
        this.aboutMe = profile.a;

        this.isFbLogin = profile.fb;
        this.emailValidated = profile.vl;
        this.email = profile.em;
        //this.createdDate = profile.sd;
        this.lastLogin = profile.ll;
        this.totalLogin = profile.tl;
        this.numPosts = profile.qc;
        this.numSold = profile.ac;
        this.numLikes = profile.ac;

        this.isFollowing = profile.following;
        this.numFollowers = profile.numFollowers;
        this.numFollowings = profile.numFollowings;
    }

    public void merge(UserVMLite user) {
        this.id = user.id;
        this.displayName = user.displayName;
        this.numFollowings = user.numFollowings;
        this.numFollowers = user.numFollowers;
        this.numPosts = user.numPosts;
        this.numSold = user.numSold;
        this.numLikes = user.numLikes;
        this.numCollections = user.numCollections;
        this.isFollowing = user.isFollowing;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

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

    public Long getTotalLogin() {
        return totalLogin;
    }

    public void setTotalLogin(Long totalLogin) {
        this.totalLogin = totalLogin;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isFbLogin() {
        return isFbLogin;
    }

    public void setIsFbLogin(boolean isFbLogin) {
        this.isFbLogin = isFbLogin;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    // obsolete

    public boolean isSA() {
        return isSA;
    }

    public void setIsSA(boolean isSA) {
        this.isSA = isSA;
    }

    public boolean isBA() {
        return isBA;
    }

    public void setIsBA(boolean isBA) {
        this.isBA = isBA;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setIsCA(boolean isCA) {
        this.isCA = isCA;
    }

    public boolean isE() {
        return isE;
    }

    public void setIsE(boolean isE) {
        this.isE = isE;
    }

    public Long getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Long questionsCount) {
        this.questionsCount = questionsCount;
    }

    public Long getAnswersCount() {
        return answersCount;
    }

    public void setAnswersCount(Long answersCount) {
        this.answersCount = answersCount;
    }

    public boolean isEnableSignInForToday() {
        return enableSignInForToday;
    }

    public void setEnableSignInForToday(boolean enableSignInForToday) {
        this.enableSignInForToday = enableSignInForToday;
    }

    @Override
    public String toString() {
        return "id=" + id + "\n" +
                "email=" + email + "\n" +
                "emailValidated=" + emailValidated + "\n" +
                "fbLogin=" + isFbLogin + "\n" +
                "signupDate=" + createdDate + "\n" +
                "lastLogin=" + lastLogin + "\n" +
                "totalLogin=" + totalLogin + "\n" +
                "numFollowers=" + numFollowers + "\n" +
                "numFollowings=" + numFollowings + "\n" +
                "numPosts=" + numPosts + "\n" +
                "numSold=" + numSold;
    }
}

