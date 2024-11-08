package com.example.rocket_launch;

import org.junit.Before;
import org.junit.Test;

public class waitingListTest {
    private EventsDB db;
    private String testEventID = "testEvent";
    private String testUserID = "testUser";

    @Before
    public void setUp() {
        db = new EventsDB();
        Event newEvent = new Event(testEventID, "Test Event1", "Testing adding event", null, null, 20, null, 3);
        //db.addEvent(newEvent);
    }


    @Test
    public void testAddUserToWaitingList() {
        db.addUserToWaitingList(testEventID, testUserID);
    }

    @Test
    public void testRemoveUserFromWaitingList() {
        db.removeUserFromWaitingList(testEventID, testUserID);

    }

    @Test
    public void testWaitingListLimit() {
        db.addUserToWaitingList(testEventID, "user1");
        db.addUserToWaitingList(testEventID, "user2");
        db.addUserToWaitingList(testEventID, "user3");
        db.addUserToWaitingList(testEventID, "user4");

    }

}
