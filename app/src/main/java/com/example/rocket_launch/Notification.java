package com.example.rocket_launch;

///**
// * class for notification data
// */
//public class Notification {
//    private String sender; // probably string for name of event created
//    private String message;
//    private String messageType; // guessing at the type here
//
//    /**
//     * empty constructor
//     */
//    public Notification() {
//
//    }
//}


public class Notification {
    private String id;
    private String title;
    private String message;
    private String eventId;

    private Boolean invitation;

    // No-argument constructor (required for Firestore)
    public Notification() {}

    // Full constructor (non-invite)
    public Notification(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.invitation = false;
    }

    public void createInvite(String id, String title, String eventId) {
        this.id = id;
        this.title = title;
        this.eventId = eventId;
        this.invitation = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getInvitation() {
        return invitation;
    }

    public void setInvitation(Boolean invitation) {
        this.invitation = invitation;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
