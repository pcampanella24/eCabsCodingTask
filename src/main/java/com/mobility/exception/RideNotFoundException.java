package com.mobility.exception;

public class RideNotFoundException extends RideMatchingException {

    private final String rideId;

    public RideNotFoundException(String rideId) {
        super("Ride not found: " + rideId);
        this.rideId = rideId;
    }

    public String getRideId() {
        return rideId;
    }
}