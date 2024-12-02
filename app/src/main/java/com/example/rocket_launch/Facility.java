package com.example.rocket_launch.data;

/**
 * Represents a Facility with a name, address, and unique user ID.
 * Author: Pouyan
 */
public class Facility {
    private String facilityName;
    private String facilityAddress;
    private String userId;

    public Facility() {}

    public Facility(String facilityName, String facilityAddress, String userId) {
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
