package com.licence.pocketteacher.aiding_classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String university;
    private String profileDescription;
    private String profileImageBase64;
    private String privacy;

    // For students side of application
    public Student(String username, String firstName, String lastName, String email, String gender, String university, String profileDescription, String profileImageBase64, String privacy){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.university = university;
        this.profileDescription = profileDescription;
        this.profileImageBase64 = profileImageBase64;
        this.privacy = privacy;
    }

    // For teacher side of application
    public Student(String username, String firstName, String lastName, String gender, String university, String profileImageBase64){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.university = university;
        this.profileImageBase64 = profileImageBase64;
    }

    public Student(){
        this.username = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.gender = null;
        this.university = null;
        this.profileDescription = null;
        this.profileImageBase64 = null;
        this.privacy = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }

    public String getPrivacy() { return privacy; }

    public void setPrivacy(String privacy){
        this.privacy = privacy;
    }

    public String getUniversity() { return university; }

    public void setUniversity(String university) { this.university = university; }


    /*                                 *** P A R C E L A B L E  ***                               */
    protected Student(Parcel in) {
        username = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        gender = in.readString();
        university = in.readString();
        profileDescription = in.readString();
        profileImageBase64 = in.readString();
        privacy = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(university);
        dest.writeString(profileDescription);
        dest.writeString(profileImageBase64);
        dest.writeString(privacy);
    }
}
