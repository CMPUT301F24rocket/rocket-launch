package com.example.rocket_launch;

import java.util.HashMap;

public class Roles {
    boolean organizer;
    boolean entrant;
    boolean admin;

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

    public void fromDB(HashMap vals) {
        this.entrant = (Boolean) vals.get("entrant");
        this.organizer = (Boolean) vals.get("organizer");
        this.admin = (Boolean) vals.get("admin");
    }
}
