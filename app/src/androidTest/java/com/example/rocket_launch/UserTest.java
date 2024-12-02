package com.example.rocket_launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class UserTest {
    private User user;
    private Event event;

    /**
     * Tests the user functions for related user stories
     * Author: Griffin
     */
    @Before
    public void setUp(){
        user = new User();
        event = new Event();
        event.setQRCode("QR test");
        event.setName("event1");
        event.setDescription("test event1");
        event.setCapacity(5);
        event.setMaxWaitlistSize(10);
    }

    @Test
    /**
     * Join and leave the waiting list (US 01.01.01, US 01.01.02)
     */
    public void testAddAndRemoveWaitlist(){
        user.addWaitlistEvent("event1");
        assertTrue(user.getEventsWaitlisted().contains("event1"));

        user.removeWaitlistEvent("event1");
        assertFalse(user.getEventsWaitlisted().contains("event1"));
    }

    @Test
    /**
     *  retrieve and get user name (US 01.02.01, US 01.02.02)
     */
    public void testSetAndGetUserName(){
        user.setUserName("John Smith");
        assertEquals("John Smith", user.getUserName());
    }

    @Test
    /**
     *  retrieve and get user email (US 01.02.01, US 01.02.02)
     */
    public void testSetAndGetUserEmail() {
        user.setUserEmail("jsmith@ualberta.ca");
        assertEquals("jsmith@ualberta.ca", user.getUserEmail());
    }

    @Test
    /**
     * retrieve and get user phone number (US 01.02.01, US 01.02.02)
     */
    public void testSetAndGetUserPhoneNumber() {
        user.setUserPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", user.getUserPhoneNumber());
    }

    @Test
    /**
     * upload and remove a profile picture (US 01.03.01, US 01.03.02)
     */
    public void testSetAndGetProfilePicture(){
        user.setProfilePhotoPath("user/images/johsmith.jpg");
        assertEquals("user/images/johsmith.jpg", user.getProfilePhotoPath());

        user.setProfilePhoto(null);
        assertNull(user.getProfilePhoto());
    }

    @Test
    /**
     * receive notification when chosen and not chosen (US 01.04.01, US 01.04.02)
     */
    public void testReceiveNotification(){
        Notification notification = new Notification("1", "Congratulations!", "You won the lottery.");
        assertEquals("1", notification.getId());
        assertEquals("Congratulations!", notification.getTitle());
        assertEquals("You won the lottery.", notification.getMessage());

        Notification failNotification = new Notification("2", "Sorry", "You were not selected.");
        assertEquals("2", failNotification.getId());
        assertEquals("Sorry", failNotification.getTitle());
        assertEquals("You were not selected.", failNotification.getMessage());
    }

    @Test
    /**
     * opt in and opt out of getting notification (US 01.04.03)
     */
    public void testNotificationBehavior(){
        user.optInNotifications();
        assertTrue(user.getNotificationPreferences());

        user.optOutNotifications();
        assertFalse(user.getNotificationPreferences());
    }

    @Test
    /**
     * another user is given the chance to join if someone declines (US 01.05.01)
     */
    public void testAnotherChanceAfterDecline() {
        event.addToWaitingList("user1");
        event.addToWaitingList("user2");

        // user1 declines
        event.declineInvitation("user1");
        assertFalse(event.getWaitingList().contains("user1"));

        // user2 accepts
        List<String> invitedUsers = event.sampleWaitlist(1);
        assertTrue(invitedUsers.contains("user2"));
        assertFalse(event.getWaitingList().contains("user2"));
    }

    @Test
    /**
     * user can accepts invitation (US 01.05.02)
     */
    public void testAcceptInvitation(){
        event.addToWaitingList("user1");
        int startingParticipants = event.getParticipants();
        boolean accepted = event.acceptInvitation("user1");
        if(accepted){
            event.setParticipants(event.getParticipants()+1);
        }
        assertTrue(accepted);
        assertEquals(startingParticipants + 1, event.getParticipants());
    }

    @Test
    /**
     * user can decline invitation (US 01.05.03)
     */
    public void testDeclineInvitation(){
        event.addToWaitingList("user1");
        event.declineInvitation("user1");
        assertFalse(event.getWaitingList().contains("user1"));
    }

    @Test
    /**
     * scanning QR code retrieves event details (US 01.06.01)
     */
    public void testEventDetailsQRcode(){
        //check if you can make a QR code
        Bitmap QRcode = event.generateQRCode();
        assertNotNull(QRcode);
        assertEquals(500, QRcode.getWidth());
        assertEquals(500, QRcode.getHeight());

        assertEquals("event1", event.getName());
        assertEquals("test event1", event.getDescription());
        assertEquals("QR test", event.getQRCode());
    }

    @Test
    public void testSignUpForEventUsingQRCode() {
        // Step 1: Generate QR code
        Bitmap QRcode = event.generateQRCode();
        assertNotNull(QRcode);
        assertEquals(500, QRcode.getWidth());
        assertEquals(500, QRcode.getHeight());

        String scannedQRCode = event.getQRCode();
        assertEquals("QR test", scannedQRCode);

        if (scannedQRCode != null && event.getWaitingList().size() < event.getMaxWaitlistSize()) {
            event.addToWaitingList("user1");
        }

        assertTrue(event.getWaitingList().contains("user1"));
    }

    @Test
    /**
     * identified by device (US 01.07.01)
     */
    public void testSetAndGetAndroidID(){
        user.setAndroidId("12345ABC");
        assertEquals("12345ABC", user.getAndroidId());
    }

    @Test
    /**
     * warned before joining a waiting list with geolocation (US 01.08.01)
     */
    public void testWarnedGeolocation(){
        event.setGeolocationRequired(true);
        boolean acceptsWarning = true; // user accepts
        if(event.getGeolocationRequired() && acceptsWarning){
            event.addToWaitingList("user1");
        }
        assertTrue(event.getWaitingList().contains("user1"));
    }
}
