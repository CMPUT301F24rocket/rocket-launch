package com.example.rocket_launch;

/**
 * Defines a new class that contains all of a given user's roles
 */
public class Roles {
    boolean organizer;
    boolean entrant;
    boolean admin;

    /**
     * constructor
     */
    public Roles() {
        this.admin = false;
        this.entrant = false;
        this.organizer = false;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isOrganizer() {
        return organizer;
    }

    public void setOrganizer(boolean organizer) {
        this.organizer = organizer;
    }

    public boolean isEntrant() {
        return entrant;
    }

    public void setEntrant(boolean entrant) {
        this.entrant = entrant;
    }
}
