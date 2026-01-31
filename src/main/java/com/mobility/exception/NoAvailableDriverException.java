package com.mobility.exception;

import com.mobility.model.Location;

public class NoAvailableDriverException extends RideMatchingException {

    private final Location pickupLocation;

    public NoAvailableDriverException(Location pickupLocation) {
        super("No available drivers found near location: " + pickupLocation);
        this.pickupLocation = pickupLocation;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }
}