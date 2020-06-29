package com.licence.pocketteacher.aiding_classes;


import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

public class TextMessage {

    private String username;
    private String message;
    private String date;
    private int type; // 0 - student, 1 - teacher

    public TextMessage(String username, String message, int type, String usernameReceiver) {
        this.username = username;
        this.message = message;
        this.type = type;
        date = HelpingFunctions.getLastSenderMessageDate(username, usernameReceiver).getFullDate();

    }

    public TextMessage(String username, String message, String date, int type) {
        this.username = username;
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public TextMessage(){
        this.username = null;
        this.message = null;
        this.date = null;
        this.type = -1;
    }

    public TextMessage(String date){
        this.date = date;
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

    public String getExactHour(){
        return date.substring(11, 19);
    }

    public String getFullDate(){
        return date;
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
