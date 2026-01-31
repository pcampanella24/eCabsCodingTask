package com.mobility.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Objects;

public class Driver {

    private final String driverId;
    private final String name;
    private volatile Location currentLocation;
    private final AtomicBoolean available;

    public Driver(String driverId, String name, Location currentLocation) {
        validateInputs(driverId, name, currentLocation);
        this.driverId = driverId;
        this.name = name;
        this.currentLocation = currentLocation;
        this.available = new AtomicBoolean(true);
    }

    private void validateInputs(String driverId, String name, Location location) {
        if (driverId == null || driverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be null or empty");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
    }

    public String getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.currentLocation = location;
    }

    public boolean isAvailable() {
        return available.get();
    }

    // Returns true only if driver was successfully marked unavailable
    public boolean markAsUnavailable() {
        return available.compareAndSet(true, false);
    }

    public void markAsAvailable() {
        available.set(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return driverId.equals(driver.driverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId);
    }

    @Override
    public String toString() {
        return String.format("Driver(%s, %s, available = %s)", driverId, name, available.get());
    }
}