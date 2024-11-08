package com.example.rocket_launch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RolesTest {

    /**
     * Is user entrant
     */
    @Test
    public void testEntrantRole() {
        Roles roles = new Roles();
        roles.setEntrant(true);

        User user = new User();
        user.setRoles(roles);

        assertTrue(user.isEntrant());
        assertFalse(user.isOrganizer());
        assertFalse(user.isAdmin());
    }

    /**
     * Is user admin
     */
    @Test
    public void testAdminRole() {
        Roles roles = new Roles();
        roles.setAdmin(true);

        User user = new User();
        user.setRoles(roles);

        assertTrue(user.isAdmin());
        assertFalse(user.isEntrant());
        assertFalse(user.isOrganizer());
    }

    /**
     * Is user organizer
     */
    @Test
    public void testOrganizerRole() {
        Roles roles = new Roles();
        roles.setOrganizer(true);

        User user = new User();
        user.setRoles(roles);

        assertTrue(user.isOrganizer());
        assertFalse(user.isEntrant());
        assertFalse(user.isAdmin());
    }
}

