package com.licence.pocketteacher.aiding_classes;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextMessage {

    private String username;
    private String message;
    private String date;
    private int type; // 0 - student, 1 - teacher

    public TextMessage(String username, String message, int type) {
        this.username = username;
        this.message = message;
        this.type = type;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        date = currentDate + " " + currentTime;
    }

    public TextMessage(String username, String message, String date, int type) {
        this.username = username;
        this.message = message;
        this.date = date;
        this.type = type;
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

    public String getDate() {
        return date.substring(11, 16);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
