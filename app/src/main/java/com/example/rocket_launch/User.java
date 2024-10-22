package com.example.rocket_launch;


public class User {

    private String android_id;
    private Roles roles;

    public User() {
        this.roles = new Roles();
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public Boolean isEntrant() {
        return this.roles.isEntrant();
    }

    public Boolean isOrganizer() {
        return this.roles.isOrganizer();
    }

    public Boolean isAdmin() {
        return this.roles.isAdmin();
    }

}
