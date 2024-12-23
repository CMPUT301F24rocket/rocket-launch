package com.example.rocket_launch;


/**
 * Class that creates an object to store entrant name and coordinates
 * to store in a list in the Event Class for mapView implementation
 * Author: Rachel
 */
public class EntrantLocationData {
    private String entrantID;
    private double entrantLatitude;
    private double entrantLongitude;

    public EntrantLocationData(){

    }

    /**
     * Constructor for Entrant location data
     * @param entrantID Gets the ID of the entrant
     * @param latitude  Gets the entrant's latitude
     * @param longitude Gets the entrant's longitude
     */
    public EntrantLocationData(String entrantID, double latitude, double longitude){
        this.entrantID = entrantID;
        this.entrantLatitude = latitude;
        this.entrantLongitude = longitude;
    }

    //Use entrantID to get entrant name in the map options entrant list
    public String getEntrantID(){return entrantID;}
    public double getEntrantLatitude(){return entrantLatitude;}
    public double getEntrantLongitude(){return entrantLongitude;}

    // Setter methods
    public void setEntrantID(String entrantID){this.entrantID = entrantID;}
    public void setEntrantLatitude(double entrantLatitude){this.entrantLatitude = entrantLatitude;}
    public void setEntrantLongitude(double entrantLongitude){this.entrantLongitude = entrantLongitude;}
}
