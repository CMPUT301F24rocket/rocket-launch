package com.example.rocket_launch;

import android.media.Image;

import java.util.Calendar;

public class Event {
    String name;
    String description;
    Calendar startTime;
    Calendar endTime;
    int participants;
    Image photo;

    Event(String name, String description, Calendar startTime, Calendar endTime, int participants, Image photo) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.photo = photo;
    }
}
