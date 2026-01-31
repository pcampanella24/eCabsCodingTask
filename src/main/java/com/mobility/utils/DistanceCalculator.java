package com.mobility.utils;

import com.mobility.model.Location;

public final class DistanceCalculator {

    private DistanceCalculator() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Calculates straight-line Euclidean distance between two locations.
     */
    public static double calculateDistance(Location from, Location to) {
        double deltaLat = from.getLatitude() - to.getLatitude();
        double deltaLon = from.getLongitude() - to.getLongitude();
        return Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon);
    }
}