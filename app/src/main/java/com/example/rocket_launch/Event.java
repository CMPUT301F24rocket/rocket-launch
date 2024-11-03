package com.example.rocket_launch;

import android.media.Image;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Event {
    private String eventID;
    private String name;
    private String description;
    private Calendar startTime;
    private Calendar endTime;
    private int participants;
    private Image photo;
    private List<String> waitingList;

    public Event(String eventID, String name, String description, Calendar startTime, Calendar endTime, int participants, Image photo) {
        this.eventID = eventID;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.photo = photo;
        this.waitingList = new ArrayList<>();
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

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public int getParticipants() {
        return participants;
    }
}
