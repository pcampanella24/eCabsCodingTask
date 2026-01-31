package com.mobility.exception;

public class DriverNotFoundException extends RideMatchingException {

    private final String driverId;

    public DriverNotFoundException(String driverId) {
        super("Driver not found: " + driverId);
        this.driverId = driverId;
    }

    public String getDriverId() {
        return driverId;
    }
}