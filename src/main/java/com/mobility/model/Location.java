package com.mobility.model;

import com.mobility.constants.RideMatchingCostants;
import com.mobility.utils.DistanceCalculator;

import java.util.Objects;

public class Location {

    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        validateCoordinates(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < RideMatchingCostants.MIN_LATITUDE || latitude > RideMatchingCostants.MAX_LATITUDE) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < RideMatchingCostants.MIN_LONGITUDE || longitude > RideMatchingCostants.MAX_LONGITUDE) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distanceTo(Location other) {
        return DistanceCalculator.calculateDistance(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
                Double.compare(location.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("Location(%.2f, %.2f)", latitude, longitude);
    }
}