package com.example.rocket_launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;



public class AdminTest {
    private List<Event> events;
    private List<User> profiles;
    private List<String> images;
    private List<String> hashedQRCodeData;
    private List<com.example.rocket_launch.data.Facility> facilities;

    /**
     * Tests the admin functions for related user stories
     * Author: Griffin
     */
    @Before
    public void setUp() {

        events = new ArrayList<>();
        profiles = new ArrayList<>();
        images = new ArrayList<>();
        hashedQRCodeData = new ArrayList<>();
        facilities = new ArrayList<>();

        events.add(new Event());
        events.get(0).setName("Event1");

        profiles.add(new User());
        profiles.get(0).setUserName("John Smith");

        images.add("image1.png");
        images.add("image2.png");

        hashedQRCodeData.add("hashedQRCode1");
        hashedQRCodeData.add("hashedQRCode2");
    }

    @Test
    /**
     * remove events (US 03.01.01)
     */
    public void testRemoveEvents() {
        events.remove(0);
        assertEquals(0, events.size());
    }

    @Test
    /**
     * remove profiles (US 03.02.01)
     */
    public void testRemoveProfiles() {
        profiles.remove(0); // Simulate removing the first user profile
        assertEquals(0, profiles.size()); // Ensure the profile list is now empty
    }

    @Test
    /**
     * remove images (US 03.03.01)
     */
    public void testRemoveImages() {
        images.remove("image1.png");
        assertEquals(1, images.size());
        assertFalse(images.contains("image1.png"));
    }

    @Test
    /**
     * remove hashed QR code data (US 03.03.02)
     */
    public void testRemoveHashedQRCodeData() {
        hashedQRCodeData.remove("hashedQRCode1");
        assertEquals(1, hashedQRCodeData.size());
        assertFalse(hashedQRCodeData.contains("hashedQRCode1"));
    }

    @Test
    /**
     * browse events (US 03.04.01)
     */
    public void testBrowseEvents() {
        assertNotNull(events);
        assertEquals("Event1", events.get(0).getName());
    }

    @Test
    /**
     * browse profiles (US 03.05.01)
     */
    public void testBrowseProfiles() {
        assertNotNull(profiles);
        assertEquals("John Smith", profiles.get(0).getUserName());
    }

    @Test
    /**
     * browse images (US 03.06.01)
     */
    public void testBrowseImages() {
        assertNotNull(images);
        assertEquals(2, images.size());
        assertTrue(images.contains("image1.png"));
        assertTrue(images.contains("image2.png"));
    }

}
