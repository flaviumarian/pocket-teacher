package com.licence.pocketteacher.aiding_classes;

public class Post {

    private String username;
    private String profileImageBase64;
    private String gender;
    private String name;
    private String timeSince;
    private String title;
    private String subject;
    private String folder;
    private String description;
    private String likes;
    private String comments;
    private String likedStatus;
    private String fileUrl;
    private boolean isLastPost;

    public Post(String username, String profileImageBase64, String gender, String name, String timeSince, String title, String subject, String folder, String description, String likes, String comments, String likedStatus, String fileUrl) {
        this.username = username;
        this.profileImageBase64 = profileImageBase64;
        this.gender = gender;
        this.name = name;
        this.timeSince = timeSince;
        this.title = title;
        this.subject = subject;
        this.folder = folder;
        this.description = description;
        this.likes = likes;
        this.comments = comments;
        this.likedStatus = likedStatus;
        this.fileUrl = fileUrl;
    }

    public Post(boolean isLastPost){
        this.isLastPost = isLastPost;
    }

    public Post(String fileName, String likedStatus, String likes, String comments){
        this.title = fileName;
        this.likedStatus = likedStatus;
        this.likes = likes;
        this.comments = comments;
    }


    public boolean isLastPost() {
        return isLastPost;
    }

    public void setLastPost(boolean lastPost) {
        isLastPost = lastPost;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeSince() {
        return timeSince;
    }

    public void setTimeSince(String timeSince) {
        this.timeSince = timeSince;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLikedStatus() {
        return likedStatus;
    }

    public void setLikedStatus(String likedStatus) {
        this.likedStatus = likedStatus;
    }
}
