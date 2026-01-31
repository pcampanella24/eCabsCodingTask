package com.mobility.constants;

public class RideMatchingCostants {

    private RideMatchingCostants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static final double MIN_LATITUDE = -90.00;
    public static final double MAX_LATITUDE = 90.00;
    public static final double MIN_LONGITUDE = -180.00;
    public static final double MAX_LONGITUDE = 180.00;


    public static final int MAX_ALLOCATION_RETRIES = 5;
    public static final String RIDE_ID_PREFIX = "RIDE-";
}