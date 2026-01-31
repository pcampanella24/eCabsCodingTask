package com.mobility.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void constructorValidCoordinatesCreatesLocation() {
        Location location = new Location(40.95, -74.60);

        assertEquals(40.95, location.getLatitude());
        assertEquals(-74.60, location.getLongitude());
    }

    @Test
    void constructorInvalidLatitudeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Location(91.00, -74.10));
        assertThrows(IllegalArgumentException.class, () -> new Location(-91.00, -74.20));
    }

    @Test
    void constructorInvalidLongitudeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Location(40.30, 181.00));
        assertThrows(IllegalArgumentException.class, () -> new Location(40.50, -181.00));
    }

    @Test
    void distanceToSameLocationReturnsZero() {
        Location loc = new Location(40.71, -74.60);

        assertEquals(0.0, loc.distanceTo(loc));
    }

    @Test
    void distanceToDifferentLocationCalculatesCorrectDistance() {
        Location loc1 = new Location(0.00, 0.00);
        Location loc2 = new Location(3.00, 4.00);

        double distance = loc1.distanceTo(loc2);

        assertEquals(5.00, distance);
    }

    @Test
    void distanceToSymmetricDistance() {
        Location loc1 = new Location(40.20, -74.70);
        Location loc2 = new Location(41.30, -73.40);

        double dist1to2 = loc1.distanceTo(loc2);
        double dist2to1 = loc2.distanceTo(loc1);

        assertEquals(dist1to2, dist2to1);
    }

    @Test
    void equalsSameCoordinatesReturnsTrue() {
        Location loc1 = new Location(40.75, -74.60);
        Location loc2 = new Location(40.75, -74.60);

        assertEquals(loc1, loc2);
    }

    @Test
    void equalsDifferentCoordinatesReturnsFalse() {
        Location loc1 = new Location(40.75, -74.60);
        Location loc2 = new Location(40.80, -74.60);

        assertNotEquals(loc1, loc2);
    }

    @Test
    void hashCodeSameCoordinatesSameHashCode() {
        Location loc1 = new Location(40.30, -74.50);
        Location loc2 = new Location(40.30, -74.50);

        assertEquals(loc1.hashCode(), loc2.hashCode());
    }

    @Test
    void toStringContainsCoordinates() {
        Location location = new Location(40.75, -74.60);
        String str = location.toString();

        assertTrue(str.contains("40.75"));
        assertTrue(str.contains("-74.60"));
    }
}