package com.example.rocket_launch.data;

public class Facility {
    private String facilityName;
    private String facilityAddress;

    // Empty constructor for Firebase
    public Facility() {}

    public Facility(String facilityName, String facilityAddress) {
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityAddress() {
        return facilityAddress;
    }

    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }
}
