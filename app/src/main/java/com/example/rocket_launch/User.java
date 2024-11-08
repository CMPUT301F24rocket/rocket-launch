package com.example.rocket_launch;


import android.location.Location;
import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class User {

    //Android ID
    private String android_id;

    //User Profile Information
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private Image profilePhoto;
    private String userFacility;
    private Location location;
    private Roles roles;
    private Boolean notificationPreferences;
    private List<String> notifications;

    // event data
    private List<String> eventsJoined;
    private List<String> eventsWaitlisted;
    private List<String> eventsCreated;


    public User() {
        this.roles = new Roles();
        this.eventsJoined = new ArrayList<>();
        this.eventsCreated = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    // Username
    String getUserName() {return this.userName;}
    public void setUserName(String userName) {this.userName = userName;}

    // user email
    String getUserEmail() {return this.userEmail;}
    public void setUserEmail(String userEmail) {this.userEmail = userEmail;}

    // user phone number
    String getUserPhoneNumber() {return this.userPhoneNumber;}
    public void setUserPhoneNumber(String userPhoneNumber) {this.userPhoneNumber = userPhoneNumber;}

    // profile photo
    Image getProfilePhoto() {return this.profilePhoto;}
    public void setProfilePhoto(Image profilePhoto){this.profilePhoto = profilePhoto;}

    // facility
    String getUserFacility(){return  this.userFacility;}
    public void setUserFacility(String userFacility){this.userFacility = userFacility;}

    // android id
    String getAndroid_id() {return this.android_id;}
    public void setAndroid_id(String android_id) {this.android_id = android_id;}

    // roles
    public Roles getRoles() {return roles;}
    public void setRoles(Roles roles) {this.roles = roles;}

    // notifications
    public List<String> getNotifications() {return notifications;}
    public void setNotifications(List<String> notifications) {this.notifications = notifications;}
    public void addNotification(String notification){notifications.add(notification);}

    // notification preferences
    public Boolean getNotificationPreferences() {return notificationPreferences;}
    public void setNotificationPreferences(Boolean notificationPreferences) {this.notificationPreferences = notificationPreferences;}

    // events joined
    public List<String> getEventsJoined() {return eventsJoined;}
    public void setEventsJoined(List<String> eventsJoined) {this.eventsJoined = eventsJoined;}
    public void addJoinedEvent(String id) {eventsJoined.add(id);};
    public void removeJoinedEvent(String id) {eventsJoined.remove(id);}

    // events created
    public List<String> getEventsCreated() {return eventsCreated;}
    public void setEventsCreated(List<String> eventsCreated) {this.eventsCreated = eventsCreated;}
    public void addCreatedEvent(String id) {eventsCreated.add(id);};
    public void removeCreatedEvent(String id) {eventsCreated.remove(id);}

    // events waitlisted
    public List<String> getEventsWaitlisted() {return eventsWaitlisted;}
    public void setEventsWaitlisted(List<String> eventsWaitlisted) {this.eventsWaitlisted = eventsWaitlisted;}
    public void addWaitlistEvent(String id) {eventsWaitlisted.add(id);}
    public void removeWaitlistEvent(String id) {eventsWaitlisted.remove(id);}
}
