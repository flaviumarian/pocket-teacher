package com.licence.pocketteacher.aiding_classes;

public class Comment {
    private String type;
    private String username;
    private String imageBase64;
    private String gender;
    private String comment;
    private String timeSince;
    private String timestamp;
    private int likes;
    private String likedStatus;

    public Comment(String type, String username, String imageBase64, String gender, String comment, String timeSince, String timestamp, int likes, String likedStatus) {
        this.type = type;
        this.username = username;
        this.imageBase64 = imageBase64;
        this.gender = gender;
        this.timeSince = timeSince;
        this.timestamp = timestamp;
        this.comment = comment;
        this.likes = likes;
        this.likedStatus = likedStatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) { this.type = type; }

    public String getType() { return type; }

    public void setUsername(String username) { this.username = username; }

    public String getUsername() { return username; }

    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getImageBase64() { return imageBase64; }

    public void setGender(String gender) { this.gender = gender; }

    public String getGender() { return gender; }

    public void setComment(String comment) { this.comment = comment; }

    public String getComment() { return comment; }

    public void setTimeSince(String timeSince) { this.timeSince = timeSince; }

    public String getTimeSince() { return timeSince; }

    public void setLikes(int likes) { this.likes = likes; }

    public int getLikes() { return likes; }

    public void setLikedStatus(String likedStatus) { this.likedStatus = likedStatus; }

    public String getLikedStatus() { return likedStatus; }

}
