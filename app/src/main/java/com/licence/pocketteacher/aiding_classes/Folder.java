package com.licence.pocketteacher.aiding_classes;

public class Folder implements Comparable<Folder>{

    private String name;
    private boolean showPosts;

    public Folder(String name) {

        this.name = name;
        this.showPosts = false;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowPosts() {
        return showPosts;
    }

    public void setShowPosts(boolean showPosts) {
        this.showPosts = showPosts;
    }


    @Override
    public int compareTo(Folder folder) {
        return getName().compareToIgnoreCase(folder.getName());
    }
}
