package com.mobility.exception;

import com.mobility.enums.RideStatus;

public class InvalidRideStateException extends RideMatchingException {

    private final String rideId;
    private final RideStatus currentStatus;

    public InvalidRideStateException(String rideId, RideStatus currentStatus, String operation) {
        super(String.format("Cannot %s ride %s in status %s", operation, rideId, currentStatus));
        this.rideId = rideId;
        this.currentStatus = currentStatus;
    }

    public String getRideId() {
        return rideId;
    }

    public RideStatus getCurrentStatus() {
        return currentStatus;
    }
}