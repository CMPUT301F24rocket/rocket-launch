package com.example.rocket_launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;

import com.example.rocket_launch.data.Facility;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the organizer functions for related user stories
 * Author: Griffin
 */
public class OrganizerTest {

    @Test
    /**
     * create a new event, generate QR code, and poster (US 02.01.01)
     */
    public void testCreateEvent(){
        Event event = new Event();
        event.setName("test event1");
        event.setDescription("this is a test event1");
        event.setQRCode("QR test1");
        event.setPosterUrl("poster test1");

        Bitmap qrCode = event.generateQRCode();
        assertNotNull(qrCode);
        assertEquals(500, qrCode.getWidth());
        assertEquals(500, qrCode.getHeight());

        assertEquals("test event1", event.getName());
        assertEquals("this is a test event1", event.getDescription());
        assertEquals("QR test1", event.getQRCode());
        assertEquals("poster test1", event.getPosterUrl());
    }

    @Test
    /**
     * create and manage facility profile (US 02.01.03)
     */
    public void testCreateAndManageFacility(){

        Facility facility = new Facility();
        facility.setFacilityName("test1 facility");
        facility.setFacilityAddress("123 street");

        assertEquals("test1 facility", facility.getFacilityName());
        assertEquals("123 street", facility.getFacilityAddress());
    }

    @Test
    /**
     * View list of entrants in waiting list (US 02.02.01)
     */
    public void testViewEntrantWaitingList(){
        Event event = new Event();
        event.addToWaitingList("user1");
        event.addToWaitingList("user2");

        List<String> waitingList = event.getWaitingList();
        assertEquals(2, waitingList.size());
        assertTrue(waitingList.contains("user1"));
        assertTrue(waitingList.contains("user2"));
    }

    @Test
    /**
     * validate entrant location (US 02.02.02)
     */
    public void testViewEntrantLocationsOnMap() {
        Event event = new Event();

        event.addToEntrantLocationDataList(new EntrantLocationData("user1", 51.0447, -114.0719)); // Calgary
        event.addToEntrantLocationDataList(new EntrantLocationData("user2", 53.5461, -113.4937)); // Edmonton

        List<EntrantLocationData> locationData = event.getEntrantLocationDataList();
        assertNotNull(locationData);
        assertEquals(2, locationData.size());
        assertEquals("user1", locationData.get(0).getEntrantID());
        assertEquals(51.0447, locationData.get(0).getEntrantLatitude(), 0.001);
        assertEquals(-114.0719, locationData.get(0).getEntrantLongitude(), 0.001);
    }

    @Test
    /**
     * turn on/off geolocation (US 02.02.03)
     */
    public void testEnableDisableGeolocationRequirement() {
        Event event = new Event();

        event.setGeolocationRequired(true);
        assertTrue(event.getGeolocationRequired());

        event.setGeolocationRequired(false);
        assertFalse(event.getGeolocationRequired());
    }

    @Test
    /**
     * limit waitlist size (US 02.03.01)
     */
    public void testLimitWaitingListSize() {
        Event event = new Event();
        event.setMaxWaitlistSize(2);

        if (event.getWaitingList().size() < event.getMaxWaitlistSize()) {
            event.addToWaitingList("user1");
        }
        if (event.getWaitingList().size() < event.getMaxWaitlistSize()) {
            event.addToWaitingList("user2");
        }
        if (event.getWaitingList().size() < event.getMaxWaitlistSize()) {
            event.addToWaitingList("user3");
        }

        List<String> waitingList = event.getWaitingList();
        assertEquals(2, waitingList.size());
        assertTrue(waitingList.contains("user1"));
        assertTrue(waitingList.contains("user2"));
        assertFalse(waitingList.contains("user3"));
    }

    @Test
    /**
     * upload and change event poster (US 02.04.01)
     */
    public void testUploadAndUpdateEventPoster() {
        Event event = new Event();

        event.setPosterUrl("poster1.png");
        assertEquals("poster1.png", event.getPosterUrl());

        event.setPosterUrl("poster2.png");
        assertEquals("poster2.png", event.getPosterUrl());
    }

    @Test
    /**
     * send winning lottery notification (US 02.05.01)
     */
    public void testNotifyChosenEntrants() {
        Notification notification = new Notification();
        notification.createInvite("1", "You Won!", "123");

        assertEquals("You Won!", notification.getTitle());
        assertEquals("123", notification.getEventId());
        assertTrue(notification.getInvitation());
    }


//    @Test
//    /**
//     * sample a number of attendees (US 02.05.02)
//     */
////    public void testSampleAttendees() {
////        Event event = new Event();
////        event.addToWaitingList("user1");
////        event.addToWaitingList("user2");
////
////        int sampleAmount = 1;
////        if (sampleAmount > event.getWaitingList().size()) {
////            sampleAmount = event.getWaitingList().size();
////        }
////        List<String> sampledUsers = event.sampleWaitlist(sampleAmount);
////        assertEquals(sampleAmount, sampledUsers.size());
////    }

    @Test
    /**
     * draw a replacement applicant (US 02.05.03)
     */
    public void testReplaceCancelledEntrant() {
        Event event = new Event();
        event.addToWaitingList("user1");
        event.addToWaitingList("user2");

        event.declineInvitation("user1");

        List<String> invitedUsers = new ArrayList<>();
        if (!event.getWaitingList().isEmpty()) {
            invitedUsers.add(event.getWaitingList().remove(0));
        }

        assertEquals(1, invitedUsers.size());
        assertEquals("user2", invitedUsers.get(0));
        assertFalse(event.getWaitingList().contains("user2"));
    }

    @Test
    /**
     * view and manage entrants of chosen, cancelled, registered (US 02.06.01 -> US 02.06.04)
     */
    public void testViewAndManageEntrants() {
        Event event = new Event();

        // Add entrants to different lists
        event.setInvitedEntrants(List.of("user1"));
        event.setCancelledEntrants(List.of("user2"));
        event.setregisteredEntrants(List.of("user3"));

        // Verify entrants in each list
        assertEquals(1, event.getInvitedEntrants().size());
        assertEquals(1, event.getCancelledEntrants().size());
        assertEquals(1, event.getregisteredEntrants().size());
    }

    @Test
    /**
     * send notification (US 02.07.01 -> 0.2.07.03)
     */
    public void testSendNotifications() {
        NotificationHelper.sendNotification("user1", "Update", "Event details updated.");
        NotificationHelper.sendNotification("user2", "Reminder", "Don't forget to join!");

    }

}
