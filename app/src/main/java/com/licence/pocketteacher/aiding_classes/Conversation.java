package com.licence.pocketteacher.aiding_classes;



import java.util.Calendar;


public class Conversation  implements Comparable<Conversation>{

    private String username;
    private String imageBase64;
    private String gender;
    private String lastMessage;
    private int seenStatus;
    private String time;
    private int numberOfMessages;
    private int blocked;


    public Conversation(String username, String imageBase64, String gender, String lastMessage, String time, int numberOfMessages, int blocked, int seenStatus) {
        this.username = username;
        this.imageBase64 = imageBase64;
        this.gender = gender;
        setLastMessage(lastMessage);
        setTime(time);
        this.numberOfMessages = numberOfMessages;
        this.blocked = blocked;
        this.seenStatus = seenStatus;




    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        String displayedMessage;

        if(lastMessage.length() > 40){
            displayedMessage = lastMessage.substring(0, 40);
            displayedMessage += "...";

            this.lastMessage = displayedMessage;
        }else{
            this.lastMessage = lastMessage;
        }
    }

    public String getTimeSince() {

        int day = Integer.parseInt(time.substring(8,10));
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if(day == currentDay){
            return time.substring(11, 16);
        }

        if(currentDay - 1 == day){
            return "Yesterday";
        }

        return time.substring(0, 10);

    }

    public String getTime(){ return time;}

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getSeenStatus() {
        return seenStatus;
    }

    public void setSeenStatus(int seenStatus) {
        this.seenStatus = seenStatus;
    }

    @Override
    public int compareTo(Conversation o) {

        //    yyyy-MM-dd HH:mm:ss
        if(this.time.substring(0, 4).equals(o.time.substring(0, 4))){
            if(this.time.substring(5, 7).equals(o.time.substring(5, 7))){
                if(this.time.substring(8, 10).equals(o.time.substring(8, 10))){
                    if(this.time.substring(11, 13).equals(o.time.substring(11, 13))){
                       if(this.time.substring(14, 16).equals(o.time.substring(14,16))){
                           return this.time.substring(17, 19).compareTo(o.time.substring(17,19));

                       }else{
                           return this.time.substring(14, 16).compareTo(o.time.substring(14, 16));
                       }
                    }else{
                        return this.time.substring(11, 13).compareTo(o.time.substring(11, 13));
                    }
                }else{
                    return this.time.substring(8, 10).compareTo(o.time.substring(8, 10));
                }
            }else{
                return this.time.substring(5, 7).compareTo(o.time.substring(5, 7));
            }
        }else{
            return this.time.substring(0,4).compareTo(o.time.substring(0,4));
        }
    }
}
