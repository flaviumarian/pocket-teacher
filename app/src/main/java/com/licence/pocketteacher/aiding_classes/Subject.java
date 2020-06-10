package com.licence.pocketteacher.aiding_classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable, Comparable<Subject> {

    private String subjectName, subjectDescription;
    private String domainName, domainDescription;
    private String image, domainImage;

    // For usage on teacher side of the application
    public Subject(String subjectName, String subjectDescription, String domainName, String domainDescription, String image){
        this.subjectName = subjectName;
        this.subjectDescription = subjectDescription;
        this.domainName = domainName;
        this.domainDescription = domainDescription;
        this.image = image;
    }

    // For details on student side of the application
    public Subject(){
        this.subjectName = null;
        this.subjectDescription = null;
        this.domainName = null;
        this.domainDescription = null;
        this.image = null;
        this.domainImage = null;
    }


    public void setSubjectName(String subjectName){
        this.subjectName = subjectName;
    }

    public String getSubjectName(){
        return subjectName;
    }

    public void setSubjectDescription(String subjectDescription){
        this.subjectDescription = subjectDescription;
    }

    public String getSubjectDescription(){
        return subjectDescription;
    }

    public void setDomainName(String domainName){
        this.domainName = domainName;
    }

    public String getDomainName(){
        return domainName;
    }

    public void setDomainDescription(String domainDescription){  this.domainDescription = domainDescription;  }

    public String getDomainDescription(){
        return domainDescription;
    }

    public void setImage (String image) { this.image = image; }

    public String getImage() { return image;}

    public void setDomainImage(String domainImage) { this.domainImage = domainImage; }

    public String getDomainImage(){ return domainImage; }



    /*                                 *** P A R C E L A B L E  ***                               */
    protected Subject(Parcel in) {
        subjectName = in.readString();
        subjectDescription = in.readString();
        domainName = in.readString();
        domainDescription = in.readString();
        image = in.readString();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) { return new Subject(in); }

        @Override
        public Subject[] newArray(int size) { return new Subject[size]; }
    };

    @Override
    public int describeContents() {  return 0;  }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subjectName);
        dest.writeString(subjectDescription);
        dest.writeString(domainName);
        dest.writeString(domainDescription);
        dest.writeString(image);
    }

    /*                                    *** C O M P A R A B L E ***                             */
    @Override
    public int compareTo(Subject subject) {
        return getSubjectName().compareToIgnoreCase(subject.getSubjectName());
    }
}
