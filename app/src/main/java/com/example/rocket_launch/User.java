package com.example.rocket_launch;


import android.media.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * class containing all user information
 */
public class User {

    //Android ID
    private String androidId;

    //User Profile Information
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userFacility;
    private String userFacilityAddress;
    private Roles roles;

    // profile photo
    private String profilePhotoPath;
    private Image profilePhoto;

    // notifications
    private Boolean notificationPreferences;
    private List<Notification> notifications;
    // buffer for new notifications to go to
    private List<Notification> newNotifications;

    // event data
    private List<String> eventsRegistered;
    private List<String> eventsWaitlisted;
    private List<String> eventsCreated;


    public User() {
        this.roles = new Roles();
        this.eventsRegistered = new ArrayList<>();
        this.eventsWaitlisted = new ArrayList<>();
        this.eventsCreated = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.newNotifications = new ArrayList<>();

        this.userName = "";
        this.userEmail = "";
        this.userPhoneNumber = "";
        this.userFacility = "";
        this.userFacilityAddress = "";
        this.profilePhotoPath = "";
    }

    // Username
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    // user email
    public String getUserEmail() {
        return this.userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // user phone number
    public String getUserPhoneNumber() {
        return this.userPhoneNumber;
    }
    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    // profile photo
    public Image getProfilePhoto() {
        return this.profilePhoto;
    }
    public void setProfilePhoto(Image profilePhoto){
        this.profilePhoto = profilePhoto;
    }

    // facility
    public String getUserFacility(){
        return this.userFacility;
    }
    public void setUserFacility(String userFacility){
        this.userFacility = userFacility;
    }

    public String getUserFacilityAddress() {return  this.userFacilityAddress;}
    public void setUserFacilityAddress(String userFacilityAddress) {this.userFacilityAddress = userFacilityAddress;}

    // android id
    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    // roles
    public Roles getRoles() {
        return roles;
    }
    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    // notifications
    public List<Notification> getNotifications() {
        return notifications;
    }
    public void setNotifications(List<Notification> notifications) {this.notifications = notifications;}
    public void addNotification(Notification notification){notifications.add(notification);}

    // notification preferences
    public Boolean getNotificationPreferences() {
        return notificationPreferences;
    }
    public void setNotificationPreferences(Boolean notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
    public void optInNotifications() {
        notificationPreferences = true;
    }
    public void optOutNotifications() {
        notificationPreferences = false;
    }

    // events joined
    public List<String> getEventsRegistered() {
        return eventsRegistered;
    }
    public void setEventsRegistered(List<String> eventsRegistered) {
        this.eventsRegistered = eventsRegistered;
    }
    public void addJoinedEvent(String id) {
        eventsRegistered.add(id);
    };
    public void removeJoinedEvent(String id) {
        eventsRegistered.remove(id);
    }

    // events created
    public List<String> getEventsCreated() {
        return eventsCreated;
    }
    public void setEventsCreated(List<String> eventsCreated) {
        this.eventsCreated = eventsCreated;}
    public void addCreatedEvent(String id) {eventsCreated.add(id);
    };
    public void removeCreatedEvent(String id) {
        eventsCreated.remove(id);
    }

    // events waitlisted
    public List<String> getEventsWaitlisted() {
        return eventsWaitlisted;
    }
    public void setEventsWaitlisted(List<String> eventsWaitlisted) {
        this.eventsWaitlisted = eventsWaitlisted;
    }

    /**
     * adds event to waitlist
     * @param id
     *  id of event to add to
     */
    public void addWaitlistEvent(String id) {
        eventsWaitlisted.add(id);
    }

    /**
     * removes event from waitlist
     * @param id
     *  id of event to remove
     */
    public void removeWaitlistEvent(String id) {
        eventsWaitlisted.remove(id);
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public List<Notification> getNewNotifications() {
        return newNotifications;
    }

    public void setNewNotifications(List<Notification> newNotifications) {
        this.newNotifications = newNotifications;
    }
}
