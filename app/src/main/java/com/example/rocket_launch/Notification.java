package com.example.rocket_launch;

/**
 * Defines a notification class
 */
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

    /**
     * creates a notification structured for an invite
     * @param id
     *  id for database
     * @param title
     *  title of notification
     * @param eventId
     *  id of of event from which the invite came from
     */
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
