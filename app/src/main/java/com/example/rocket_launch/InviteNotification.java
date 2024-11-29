package com.example.rocket_launch;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.provider.Settings;

public class InviteNotification extends Notification {

    public String eventId;
    private EventsDB eventsDB;

    InviteNotification() {
        eventsDB = new EventsDB();
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
