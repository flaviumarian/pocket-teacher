package com.licence.pocketteacher.aiding_classes;



import com.licence.pocketteacher.miscellaneous.HelpingFunctions;

import java.util.Calendar;

public class TextMessage {

    private String username;
    private String message;
    private String date;
    private int type; // 0 - student, 1 - teacher
    private int seenStatus;
    private String dateSeen;
    private long timestamp; // for notification constructor
    private String messagingId; // for notification id
    private boolean showTime = false;

    public TextMessage(String username, String message, int type, String usernameReceiver) {
        this.username = username;
        this.message = message;
        this.type = type;
        date = HelpingFunctions.getLastMessageSent(username, usernameReceiver).getFullDate();

    }

    public TextMessage(String username, String message, String date, int type, int seenStatus, String dateSeen) {
        this.username = username;
        this.message = message;
        this.date = date;
        this.type = type;
        this.seenStatus = seenStatus;
        this.dateSeen = dateSeen;
    }



    public TextMessage() {
        this.username = null;
        this.message = null;
        this.date = null;
        this.type = -1;
    }

    public TextMessage(String date) {
        this.date = date;
    }

    // Get last message
    public TextMessage(String message, String date, int seenStatus, int type, String dateSeen) {
        this.message = message;
        this.date = date;
        this.seenStatus = seenStatus;
        this.type = type;
        this.dateSeen = dateSeen;
    }

    // Get last sender message
    public TextMessage(String date, int seenStatus, String dateSeen) {
        this.date = date;
        this.seenStatus = seenStatus;
        this.dateSeen = dateSeen;
    }

    // For notification
    public TextMessage(String username, String message, String messagingId) {
        this.username = username;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.messagingId = messagingId;
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

    public String getHourAndMinutes() {
        return date.substring(11, 16);
    }

    public String getHourMinutesAndSeconds() {
        return date.substring(11, 19);
    }

    public String getDate() {
        return date.substring(0, 10);
    }

    public String getFullDate() {
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

    public int getSeenStatus() {
        return seenStatus;
    }

    public void setSeenStatus(int seenStatus) {
        this.seenStatus = seenStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessagingId() {
        return messagingId;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public String getDateSeen() {
        return dateSeen;
    }

    public void setDateSeen(String dateSeen) {
        this.dateSeen = dateSeen;
    }

    public String getMessageStatusForDisplay(){
        if(seenStatus == 0){
            return "Sent";
        }

        // seen status is 1
        int day = Integer.parseInt(dateSeen.substring(8,10));
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if(day == currentDay){
            return "Seen: " + dateSeen.substring(11, 16);
        }

        if(currentDay - 1 == day){
            return "Seen: Yesterday";
        }

        return "Seen: " + dateSeen.substring(0, 10);


    }
}
