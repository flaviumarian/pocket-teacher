package com.licence.pocketteacher.aiding_classes;

public class Notification {

    private String profileImageBase64;
    private String time;
    private String username;
    private String gender;
    private String message;
    private String usernamePoster, subjectName, folderName, postName;
    private String comment;

    public Notification(String profileImageBase64, String time, String username, String gender, String message, String usernamePoster, String subjectName, String folderName, String postName, String comment) {
        this.profileImageBase64 = profileImageBase64;
        this.time = time;
        this.username = username;
        this.gender = gender;
        this.message = message;
        this.usernamePoster = usernamePoster;
        this.subjectName = subjectName;
        this.folderName = folderName;
        this.postName = postName;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsernamePoster() {
        return usernamePoster;
    }

    public void setUsernamePoster(String usernamePoster) {
        this.usernamePoster = usernamePoster;
    }
}
