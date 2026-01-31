package com.mobility.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DriverTest {

    private final Location testLocation = new Location(40.75, -74.60);

    @Test
    void constructorValidParametersCreatesDriver() {
        Driver driver = new Driver("D1", "John Doe", testLocation);

        assertEquals("D1", driver.getDriverId());
        assertEquals("John Doe", driver.getName());
        assertEquals(testLocation, driver.getCurrentLocation());
        assertTrue(driver.isAvailable());
    }

    @Test
    void constructorEmptyDriverIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Driver("", "John", testLocation));
    }

    @Test
    void constructorNullNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Driver("D1", null, testLocation));
    }

    @Test
    void constructorNullLocationThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Driver("D1", "John", null));
    }

    @Test
    void setCurrentLocationValidLocationUpdatesLocation() {
        Driver driver = new Driver("D1", "John", testLocation);
        Location newLocation = new Location(41.20, -74.70);

        driver.setCurrentLocation(newLocation);

        assertEquals(newLocation, driver.getCurrentLocation());
    }

    @Test
    void setCurrentLocationNullLocationThrowsException() {
        Driver driver = new Driver("D1", "John", testLocation);

        assertThrows(IllegalArgumentException.class, () -> driver.setCurrentLocation(null));
    }

    @Test
    void markAsUnavailableAvailableDriverReturnsTrue() {
        Driver driver = new Driver("D1", "John", testLocation);

        boolean result = driver.markAsUnavailable();

        assertTrue(result);
        assertFalse(driver.isAvailable());
    }

    @Test
    void markAsUnavailableAlreadyUnavailableReturnsFalse() {
        Driver driver = new Driver("D1", "John", testLocation);
        driver.markAsUnavailable();

        boolean result = driver.markAsUnavailable();

        assertFalse(result);
        assertFalse(driver.isAvailable());
    }

    @Test
    void markAsAvailableUnavailableDriverMakesAvailable() {
        Driver driver = new Driver("D1", "John", testLocation);
        driver.markAsUnavailable();

        driver.markAsAvailable();

        assertTrue(driver.isAvailable());
    }

    @Test
    void equalsSameDriverIdReturnsTrue() {
        Driver driver1 = new Driver("D1", "John", testLocation);
        Driver driver2 = new Driver("D1", "Jane", new Location(40.85, -74.10));

        assertEquals(driver1, driver2);
    }

    @Test
    void equalsDifferentDriverIdReturnsFalse() {
        Driver driver1 = new Driver("D1", "John", testLocation);
        Driver driver2 = new Driver("D2", "John", testLocation);

        assertNotEquals(driver1, driver2);
    }

    @Test
    void hashCodeSameDriverIdSameHashCode() {
        Driver driver1 = new Driver("D1", "John", testLocation);
        Driver driver2 = new Driver("D1", "Jane", new Location(41.60, -73.30));

        assertEquals(driver1.hashCode(), driver2.hashCode());
    }

    @Test
    void toStringContainsDriverInfo() {
        Driver driver = new Driver("D1", "John", testLocation);
        String str = driver.toString();

        assertTrue(str.contains("D1"));
        assertTrue(str.contains("John"));
    }
}