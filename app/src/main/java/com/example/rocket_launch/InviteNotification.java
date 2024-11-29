package com.example.rocket_launch;

import java.util.Locale;

public class InviteNotification extends Notification {

    public String eventId;
    private EventsDB eventsDB;

    public InviteNotification() {}

    public InviteNotification(String id, String eventName) {
        setId(id);
        setTitle(String.format(Locale.CANADA, "Invited to join %s", eventName));
        setInvitation(true);
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
