package com.example.rocket_launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class eventFunctionalityTest {

    private Event mockEvent(){
        return new Event("test123", "eventTest", "event testing", null, null, 10, null, 20);

    }
    @Test
    /**
     * Test to join waiting list
     */
    public void testJoinWaitingList(){
        Event event = mockEvent();
        assertEquals(0, event.getWaitingList().size());
        event.addToWaitingList("test0");
        assertTrue(event.getWaitingList().contains("test0"));
        assertEquals(1, event.getWaitingList().size());

    }

    @Test
    /**
     * Test to leave a waiting list
     */
    public void testLeaveWaitingList(){
        Event event = mockEvent();
        event.addToWaitingList("test1");
        assertTrue(event.getWaitingList().contains("test1"));
        event.removeFromWaitingList("test1");
        assertFalse(event.getWaitingList().contains("test1"));
    }

    @Test
    /**
     * Test set and get ID
     */
    public void testSetandGetID(){
        Event event = mockEvent();
        assertEquals("test123", event.getEventID());
        event.setEventID("test321");
        assertEquals("test321", event.getEventID());
    }

    @Test
    /**
     * Test set and get string name
     */
    public void testSetandGetName(){
        Event event = mockEvent();
        assertEquals("eventTest", event.getName());
        event.setName("testEvent");
        assertEquals("testEvent", event.getName());
    }

    @Test
    /**
     * Test set and get string name
     */
    public void testSetandGetDescription(){
        Event event = mockEvent();
        assertEquals("event testing", event.getDescription());
        event.setDescription("testing event");
        assertEquals("testing event", event.getDescription());
    }

    @Test
    /**
     * Test set participation
     */
    public void testSetParticipation(){
        Event event = mockEvent();
        event.setParticipants(5);
        assertEquals(5, event.getParticipants());
    }




}
