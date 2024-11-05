package com.example.rocket_launch;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class waitingListTest {
    private EventDB db;
    private String testEventID = "testEvent";
    private String testUserID = "testUser";

    @Before
    public void setUp(){
        db = new EventDB();
    }

    @Test
    public void testAddEvent() {
        Event newEvent = new Event(testEventID, "Test Event1", "Testing adding event", null, null, 20, null);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventID", newEvent.getEventID());
        eventData.put("name", newEvent.getName());
        eventData.put("description", newEvent.getDescription());
        eventData.put("participants", newEvent.getParticipants());
        eventData.put("waitingList", newEvent.getWaitingList());

        // add event to db
        db.addEvent(testEventID, newEvent);
    }

    @Test
    public void testAddUserToWaitingList() {
        db.addUserToWaitingList(testEventID, testUserID);
    }

    @Test
    public void testRemoveUserFromWaitingList() {
        db.removeUserFromWaitingList(testEventID, testUserID);

    }

}
