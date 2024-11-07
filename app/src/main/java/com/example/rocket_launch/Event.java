package com.example.rocket_launch;

import android.media.Image;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Event {
    private String eventID;
    private String name;
    private String description;
    private int capacity;
    private boolean geolocationRequired;
    private Calendar startTime;
    private Calendar endTime;
    private List<String> participants;
    private Image photo;
    private List<String> waitingList;
    private int maxWaitingListSize; // Integer

    public Event(){

    }


    public Event(String eventID, String name, String description, Calendar startTime, Calendar endTime, Image photo, int maxWaitingListSize) {

        this.eventID = eventID;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.photo = photo;
        this.maxWaitingListSize = maxWaitingListSize;
        this.participants = new ArrayList<>();
        this.waitingList = new ArrayList<>();
    }

    public void setEventID(String eventID){this.eventID = eventID;}
    public void setName(String name){this.name = name;}
    public void setDescription(String description){this.description = description;}
    public void setCapacity(int capacity){this.capacity = capacity;}
    public void setGeolocationRequired(boolean geolocationRequired){this.geolocationRequired = geolocationRequired;}
    public void setStartTime(Calendar startTime){this.startTime = startTime;}
    public void setEndTime(Calendar endTime){this.endTime = endTime;}
    public void setParticipants(List<String> participants){ this.participants = participants; }
    public void setPhoto(Image photo){this.photo = photo;}
    public void setWaitingList(){this.waitingList = new ArrayList<>();}
    public void setMaxWaitingListSize(int maxWaitingListSize){this.maxWaitingListSize = maxWaitingListSize;}



    public int getMaxWaitingListSize() {
        return maxWaitingListSize;
    }

    public void addToWaitingList(String userID){
        waitingList.add(userID);
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public Image getPhoto() {
        return photo;
    }

    public String getEventID() {
        return eventID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCapacity() {return capacity;}

    public boolean getGeolocationRequired() {return geolocationRequired;}

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
