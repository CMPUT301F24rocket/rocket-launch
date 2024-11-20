package com.example.rocket_launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UserTest {
    private User user;

    @Before
    public void setUp(){
        user = new User();
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
     * test notification preferences (US 01.04.01)
     */
    public void testSetAndGetNotificationPreferences() {
        user.setNotificationPreferences(true);
        assertTrue(user.getNotificationPreferences());

        user.setNotificationPreferences(false);
        assertFalse(user.getNotificationPreferences());
    }

    @Test
    /**
     * Test adding notifications (01.04.02, US 01.04.03)
     */
    public void testAddNotification() {
        user.addNotification("You won the lottery!");
        assertEquals(1, user.getNotifications().size());
        assertTrue(user.getNotifications().contains("You won the lottery!"));
    }

    @Test
    /**
     * Opt in/out of notifications (US 01.04.03)
     */
    public void testNotificationPreferences() {
        user.optInNotifications();
        assertTrue(user.getNotificationPreferences());

        user.optOutNotifications();
        assertFalse(user.getNotificationPreferences());
    }

    @Test
    /**
     * Joining and leaving an event (US 01.01.01, US 01.01.02)
     */
    public void testAddAndRemoveJoinedEvent() {
        user.addJoinedEvent("event1");
        assertTrue(user.getEventsRegistered().contains("event1"));

        user.removeJoinedEvent("event1");
        assertFalse(user.getEventsRegistered().contains("event1"));
    }

    @Test
    /**
     * Removing created event (US 02.01.01)
     */
    public void testAddAndRemoveCreatedEvent() {
        user.addCreatedEvent("event2");
        assertTrue(user.getEventsCreated().contains("event2"));

        user.removeCreatedEvent("event2");
        assertFalse(user.getEventsCreated().contains("event2"));
    }

}




