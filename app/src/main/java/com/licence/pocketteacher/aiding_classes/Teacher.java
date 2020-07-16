package com.licence.pocketteacher.aiding_classes;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Teacher implements Parcelable {

    private String username, firstName, lastName, email, gender, university, profileDescription, profileImageBase64, privacy, followingStatus, followingRequestStatus, followers;
    private ArrayList<Subject> subjects;
    private ArrayList<String> subjectNames, domains;

    // for Teacher side and teacher profile view
    public Teacher(String username, String firstName, String lastName, String email, String gender, String university, String profileDescription, String profileImageBase64, String privacy, ArrayList<Subject> subjects){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.university = university;
        this.profileDescription = profileDescription;
        this.profileImageBase64 = profileImageBase64;
        this.privacy = privacy;
        this.subjects = subjects;
    }

    // For Premium teachers
    public Teacher(String username, String firstName, String lastName, String gender, String university, String profileImageBase64, String privacy, String followingStatus, String followingRequestStatus){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.university = university;
        this.profileImageBase64 = profileImageBase64;
        this.privacy = privacy;
        this.followingStatus = followingStatus;
        this.followingRequestStatus = followingRequestStatus;
    }

    // For search teachers
    public Teacher(String username, String firstName, String lastName, String gender, String university, String profileImageBase64, ArrayList<String> subjectNames, ArrayList<String> domains){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.university = university;
        this.profileImageBase64 = profileImageBase64;
        this.subjectNames = subjectNames;
        this.domains = domains;
    }

    // For search teachers when starting conversation
    public Teacher(String username, String firstName, String lastName, String gender, String university, String profileImageBase64, ArrayList<String> subjectNames, ArrayList<String> domains, String privacy, String followingStatus, String followingRequestStatus){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.university = university;
        this.profileImageBase64 = profileImageBase64;
        this.subjectNames = subjectNames;
        this.domains = domains;
        this.privacy = privacy;
        this.followingStatus = followingStatus;
        this.followingRequestStatus = followingRequestStatus;
    }

    public Teacher(){
        this.username = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.gender = null;
        this.university = null;
        this.profileDescription = null;
        this.profileImageBase64 = null;
        this.privacy = null;
        this.subjects = null;
        this.subjectNames = null;
        this.domains = null;
        this.followingStatus = null;
        this.followers = null;
        this.followingRequestStatus = null;
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

    public ArrayList<Subject> getSubjects(){ return subjects; }

    public void setSubjectNames(ArrayList<String> subjectNames) { this.subjectNames = subjectNames; }

    public ArrayList<String> getSubjectNames() { return subjectNames; }

    public void setDomains(ArrayList<String> domains){ this.domains = domains; }

    public ArrayList<String> getDomains() { return domains; }

    public void setFollowingStatus(String followingStatus) { this.followingStatus = followingStatus; }

    public String getFollowingStatus() { return followingStatus; }

    public void setFollowingRequestStatus(String followingRequestStatus) { this.followingRequestStatus = followingRequestStatus; }

    public String getFollowingRequestStatus() { return followingRequestStatus; }

    public void setFollowers(String followers) { this.followers = followers; }

    public String getFollowers() { return followers; }

    public void setSubjects(ArrayList<Subject> subjects) { this.subjects = subjects; }

    public void addSubject(String subjectName, String subjectDescription, String domainName, String domainDescription, String image){
        subjects.add(new Subject(subjectName, subjectDescription, domainName, domainDescription, image));
    }

    public  void removeSubject(String subjectName){
        for(Subject subject : subjects){
            if (subject.getSubjectName().equals(subjectName)) {
                subjects.remove(subject);
                return;
            }
        }
    }

    /*                                 *** P A R C E L A B L E  ***                               */
    protected Teacher(Parcel in) {
        username = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        gender = in.readString();
        university = in.readString();
        profileDescription = in.readString();
        profileImageBase64 = in.readString();
        privacy = in.readString();
        subjects = in.createTypedArrayList(Subject.CREATOR);
        subjectNames = in.createStringArrayList();
        domains = in.createStringArrayList();
        followingStatus = in.readString();
        followingRequestStatus = in.readString();
        followers = in.readString();
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {  return new Teacher(in);  }

        @Override
        public Teacher[] newArray(int size) {  return new Teacher[size]; }
    };

    @Override
    public int describeContents() {  return 0;  }

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
        dest.writeTypedList(subjects);
        dest.writeStringList(subjectNames);
        dest.writeStringList(domains);
        dest.writeString(followingStatus);
        dest.writeString(followingRequestStatus);
        dest.writeString(followers);
    }

}