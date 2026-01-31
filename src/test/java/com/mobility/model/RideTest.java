package com.mobility.model;

import com.mobility.enums.RideStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideTest {

    private final Location testLocation = new Location(40.75, -74.60);
    private final Driver testDriver = new Driver("D1", "John Doe", testLocation);

    @Test
    void constructorValidParametersCreatesRide() {
        Ride ride = new Ride("R1", "RIDER1", testDriver, testLocation);

        assertEquals("R1", ride.getRideId());
        assertEquals("RIDER1", ride.getRiderId());
        assertEquals(testDriver, ride.getDriver());
        assertEquals(testLocation, ride.getPickupLocation());
        assertEquals(RideStatus.IN_PROGRESS, ride.getStatus());
        assertNotNull(ride.getRequestTime());
        assertNull(ride.getCompletionTime());
    }

    @Test
    void constructorNullRideIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Ride(null, "RIDER1", testDriver, testLocation));
    }

    @Test
    void constructorNullRiderIdThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ride("R1", null, testDriver, testLocation));
    }

    @Test
    void constructorNullDriverThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Ride("R1", "RIDER1", null, testLocation));
    }

    @Test
    void constructorNullPickupLocationThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Ride("R1", "RIDER1", testDriver, null));
    }

    @Test
    void markAsCompletedSetsStatusAndCompletionTime() {
        Ride ride = new Ride("R1", "RIDER1", testDriver, testLocation);

        ride.markAsCompleted();

        assertEquals(RideStatus.COMPLETED, ride.getStatus());
        assertNotNull(ride.getCompletionTime());
        assertTrue(ride.getCompletionTime().isAfter(ride.getRequestTime()) ||
                ride.getCompletionTime().equals(ride.getRequestTime()));
    }

    @Test
    void setStatusUpdatesStatus() {
        Ride ride = new Ride("R1", "RIDER1", testDriver, testLocation);

        ride.setStatus(RideStatus.CANCELLED);

        assertEquals(RideStatus.CANCELLED, ride.getStatus());
    }

    @Test
    void equalsSameRideIdReturnsTrue() {
        Ride ride1 = new Ride("R1", "RIDER1", testDriver, testLocation);
        Ride ride2 = new Ride("R1", "RIDER2", testDriver, testLocation);

        assertEquals(ride1, ride2);
    }

    @Test
    void equalsDifferentRideIdReturnsFalse() {
        Ride ride1 = new Ride("R1", "RIDER1", testDriver, testLocation);
        Ride ride2 = new Ride("R2", "RIDER1", testDriver, testLocation);

        assertNotEquals(ride1, ride2);
    }

    @Test
    void hashCodeSameRideIdSameHashCode() {
        Ride ride1 = new Ride("R1", "RIDER1", testDriver, testLocation);
        Ride ride2 = new Ride("R1", "RIDER2", testDriver, testLocation);

        assertEquals(ride1.hashCode(), ride2.hashCode());
    }

    @Test
    void toStringContainsRideInfo() {
        Ride ride = new Ride("R1", "RIDER1", testDriver, testLocation);
        String str = ride.toString();

        assertTrue(str.contains("R1"));
        assertTrue(str.contains("RIDER1"));
        assertTrue(str.contains("D1"));
    }
}