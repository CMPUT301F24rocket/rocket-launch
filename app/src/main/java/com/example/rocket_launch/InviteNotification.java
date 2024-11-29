package com.example.rocket_launch;

import java.util.Locale;

public class InviteNotification extends Notification {

    public String eventId;
    private EventsDB eventsDB;

    public InviteNotification() {}

    public InviteNotification(String id, String eventId) {
        setId(id);
        setInvitation(true);
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void joinEvent(String userId) {
        eventsDB.addUserToRegisteredList(eventId, userId);
        eventsDB.removeUserFromInvitedList(eventId, userId);
    }
}
