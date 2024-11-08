package com.example.rocket_launch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RolesTest {

    /**
     * Test types of roles
     */

    @Test
    public void testAdminRole() {
            Roles roles = new Roles();
            roles.setAdmin(true);
            assertTrue(roles.isAdmin());
            assertFalse(roles.isEntrant());
            assertFalse(roles.isOrganizer());
        }

        @Test
        public void testEntrantRole() {
            Roles roles = new Roles();
            roles.setEntrant(true);
            assertTrue(roles.isEntrant());
            assertFalse(roles.isAdmin());
            assertFalse(roles.isOrganizer());
        }

        @Test
        public void testOrganizerRole() {
            Roles roles = new Roles();
            roles.setOrganizer(true);
            assertTrue(roles.isOrganizer());
            assertFalse(roles.isAdmin());
            assertFalse(roles.isEntrant());
        }

    }


