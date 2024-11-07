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
    private boolean notificationPreference;
    private List<String> eventsJoined;

    private List<String> notifications;

    public List<String> getNotifications() {return notifications;}

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(String notification){
        notifications.add(notification);
    }

    public void addEvent(String eventID){
        eventsJoined.add(eventID);
    }


    public List<String> getEventsJoined() {
        return eventsJoined;
    }

    //get user profile information
    String getUserName() {return this.userName;}
    String getUserEmail() {return this.userEmail;}
    String getUserPhoneNumber() {return this.userPhoneNumber;}
    Image getProfilePhoto() {return this.profilePhoto;}
    String getUserFacility(){return  this.userFacility;}

    //set user profile information
    public void setUserName(String userName) {this.userName = userName;}
    public void setUserEmail(String userEmail) {this.userEmail = userEmail;}
    public void setUserPhoneNumber(String userPhoneNumber) {this.userPhoneNumber = userPhoneNumber;}
    public void setProfilePhoto(Image profilePhoto){this.profilePhoto = profilePhoto;}
    public void setUserFacility(String userFacility){this.userFacility = userFacility;}

    //user Location Data


    //Android ID
    String getAndroid_id() {return this.android_id;}
    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    //User Roles
    public User() {
        this.roles = new Roles();
        this.eventsJoined = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }
    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public Boolean isEntrant() {
        return this.roles.isEntrant();
    }

    public Boolean isOrganizer() {
        return this.roles.isOrganizer();
    }

    public Boolean isAdmin() {
        return this.roles.isAdmin();
    }

}
