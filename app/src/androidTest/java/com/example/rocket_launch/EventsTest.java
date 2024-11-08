package com.example.rocket_launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;

import org.junit.Test;

public class EventsTest {

    private Event mockEvent(){
        return new Event("test123", "eventTest", "event testing", null, null, 10, null, 20);

    }
    /**
     * Tests for Event functionality:
     *
     * - Join waiting list: Adds a user and checks list size.
     * - Leave waiting list: Removes a user and checks list size.
     * - Set/get event ID: Tests setting and getting event ID.
     * - Set/get name: Tests setting and getting event name.
     * - Set/get description: Tests setting and getting event description.
     * - Set participation: Tests setting and getting participant count.
     */

    @Test
    /**
     * Test to join waiting list (US 01.01.01)
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
     * Test to leave a waiting list (US 01.01.02)
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
     * Test set and get ID (US 2.01.01)
     */
    public void testSetandGetID(){
        Event event = mockEvent();
        assertEquals("test123", event.getEventID());
        event.setEventID("test321");
        assertEquals("test321", event.getEventID());
    }

    @Test
    /**
     * Test set and get string name (US 2.01.01)
     */
    public void testSetandGetName(){
        Event event = mockEvent();
        assertEquals("eventTest", event.getName());
        event.setName("testEvent");
        assertEquals("testEvent", event.getName());
    }

    @Test
    /**
     * Test set and get string name (US 2.01.01)
     */
    public void testSetandGetDescription(){
        Event event = mockEvent();
        assertEquals("event testing", event.getDescription());
        event.setDescription("testing event");
        assertEquals("testing event", event.getDescription());
    }

    @Test
    /**
     * Test set participation (US 01.05.02, US 01.05.03)
     */
    public void testSetParticipation(){
        Event event = mockEvent();
        event.setParticipants(20);
        assertEquals(20, event.getParticipants());
    }

    /**
     * Generate QR code for event (US 02.01.01)
     */
    @Test
    public void testGenerateQRCode() {
        Event event = mockEvent();
        Bitmap qrCode = event.generateQRCode();
        assertNotNull("QR Code should be generated", qrCode);
    }

    @Test
    /**
     * Geoloaction rquirement warning (US 01.08.01)
     */
    public void testGeolocationRequirement(){
        Event event = mockEvent();
        event.setGeolocationRequired(true);
        assertTrue(event.isGeolocationRequired());
    }

    @Test
    /**
     * Check if you can join waitlist (US 02.03.01)
     */
    public void testWaitingListLimit(){
        Event event = mockEvent();
        event.setMaxWaitlistSize(1);
        event.addToWaitingList("test1");
        assertFalse(event.canJoinWaitingList());
    }

    /**
     * Accept or decline invitation (US 01.05.02, US 01.05.03)
     */
    @Test
    public void testAcceptAndDeclineInvitation() {
        Event event = mockEvent();
        event.setCapacity(1);
        event.addToWaitingList("testUser");
        assertTrue(event.acceptInvitation("testUser"));

        event.declineInvitation("testUser");
        assertFalse(event.getWaitingList().contains("testUser"));
    }


}
