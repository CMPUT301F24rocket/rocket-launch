package com.example.rocket_launch.data;

/**
 * Represents a Facility with a name and address.
 * Author: Pouyan
 */
public class Facility {
    private String facilityName;
    private String facilityAddress;

    /**
     * Default constructor for Firebase.
     * Author: Pouyan
     */
    public Facility() {}

    /**
     * Constructor to create a Facility with a name and address.
     *
     * @param facilityName Name of the facility.
     * @param facilityAddress Address of the facility.
     * Author: Pouyan
     */
    public Facility(String facilityName, String facilityAddress) {
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
    }

    /**
     * Gets the name of the facility.
     *
     * @return Facility name.
     * Author: Pouyan
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * Sets the name of the facility.
     *
     * @param facilityName Name to set.
     * Author: Pouyan
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * Gets the address of the facility.
     *
     * @return Facility address.
     * Author: Pouyan
     */
    public String getFacilityAddress() {
        return facilityAddress;
    }

    /**
     * Sets the address of the facility.
     *
     * @param facilityAddress Address to set.
     * Author: Pouyan
     */
    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }
}
