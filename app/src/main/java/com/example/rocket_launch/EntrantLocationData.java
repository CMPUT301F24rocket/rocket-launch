package com.example.rocket_launch;

//Class to store an entrant name and coordinate to store in a list in the Event Class for mapView implementation
public class EntrantLocationData {
    private String entrantID;
    private double entrantLatitude;
    private double entrantLongitude;

    public EntrantLocationData(){

    }

    public EntrantLocationData(String entrantID, double latitude, double longitude){
        this.entrantID = entrantID;
        this.entrantLatitude = latitude;
        this.entrantLongitude = longitude;
    }

    //Use entrantID to get entrant name in the map options entrant list
    public String getEntrantID(){return entrantID;}
    public double getEntrantLatitude(){return entrantLatitude;}
    public double getEntrantLongitude(){return entrantLongitude;}

    public void setEntrantID(String entrantID){this.entrantID = entrantID;}
    public void setEntrantLatitude(double entrantLatitude){this.entrantLatitude = entrantLatitude;}
    public void setEntrantLongitude(double entrantLongitude){this.entrantLongitude = entrantLongitude;}
}
