package com.mobility.model;

import com.mobility.enums.RideStatus;

import java.time.Instant;
import java.util.Objects;

public class Ride {

    private final String rideId;
    private final String riderId;
    private final Driver driver;
    private final Location pickupLocation;
    private final Instant requestTime;
    private volatile RideStatus status;
    private volatile Instant completionTime;

    public Ride(String rideId, String riderId, Driver driver, Location pickupLocation) {
        validateInputs(rideId, riderId, driver, pickupLocation);
        this.rideId = rideId;
        this.riderId = riderId;
        this.driver = driver;
        this.pickupLocation = pickupLocation;
        this.requestTime = Instant.now();
        this.status = RideStatus.IN_PROGRESS;
    }

    private void validateInputs(String rideId, String riderId, Driver driver, Location location) {
        if (rideId == null || rideId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ride ID cannot be null or empty");
        }
        if (riderId == null || riderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Rider ID cannot be null or empty");
        }
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        if (location == null) {
            throw new IllegalArgumentException("Pickup location cannot be null");
        }
    }

    public String getRideId() {
        return rideId;
    }

    public String getRiderId() {
        return riderId;
    }

    public Driver getDriver() {
        return driver;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Instant getRequestTime() {
        return requestTime;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Instant getCompletionTime() {
        return completionTime;
    }

    public void markAsCompleted() {
        this.status = RideStatus.COMPLETED;
        this.completionTime = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return rideId.equals(ride.rideId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rideId);
    }

    @Override
    public String toString() {
        return String.format("Ride(%s, rider = %s, driver = %s, status = %s)", rideId, riderId, driver.getDriverId(), status);
    }
}