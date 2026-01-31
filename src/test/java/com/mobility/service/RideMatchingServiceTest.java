package com.mobility.service;

import com.mobility.enums.RideStatus;
import com.mobility.exception.*;
import com.mobility.model.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RideMatchingServiceTest {

    private RideMatchingService service;
    private Location loc1;
    private Location loc2;
    private Location loc3;

    @BeforeEach
    void setup() {
        service = new RideMatchingService();
        loc1 = new Location(40.80, -74.60);
        loc2 = new Location(40.50, -73.15);
        loc3 = new Location(40.60, -73.95);
    }

    @Test
    void registerDriver() {
        Driver d = new Driver("D1", "John", loc1);
        service.registerDriver(d);

        assertEquals(1, service.getDriverCount());
        assertTrue(service.getDriver("D1").isAvailable());
    }

    @Test
    void registerNullDriver() {
        assertThrows(IllegalArgumentException.class, () -> service.registerDriver(null));
    }

    @Test
    void updateDriverLocation() {
        Driver d = new Driver("D1", "John", loc1);
        service.registerDriver(d);

        Location newLocation = new Location(41.00, -74.00);
        service.updateDriverLocation("D1", newLocation);

        assertEquals(newLocation, service.getDriver("D1").getCurrentLocation());
    }

    @Test
    void updateNonExistentDriver() {
        assertThrows(DriverNotFoundException.class, () -> service.updateDriverLocation("X", loc1));
    }

    @Test
    void requestRideSuccess() {
        Driver d = new Driver("D1", "John", loc1);
        service.registerDriver(d);

        Ride ride = service.requestRide("R1", loc1);

        assertNotNull(ride);
        assertEquals("R1", ride.getRiderId());
        assertEquals("D1", ride.getDriver().getDriverId());
        assertFalse(d.isAvailable());
        assertEquals(RideStatus.IN_PROGRESS, ride.getStatus());
    }

    @Test
    void requestRideNoDrivers() {
        assertThrows(NoAvailableDriverException.class, () -> service.requestRide("R1", loc1));
    }

    @Test
    void nearestDriverAllocation() {
        Driver d1 = new Driver("D1", "A", loc1);
        Driver d2 = new Driver("D2", "B", loc2);
        Driver d3 = new Driver("D3", "C", loc3);

        service.registerDriver(d1);
        service.registerDriver(d2);
        service.registerDriver(d3);

        Location pickup = new Location(40.75, -73.80);

        Ride ride = service.requestRide("R1", pickup);

        assertEquals("D3", ride.getDriver().getDriverId());
    }

    @Test
    void invalidRideParams() {
        assertThrows(IllegalArgumentException.class, () -> service.requestRide(null, loc1));

        assertThrows(IllegalArgumentException.class, () -> service.requestRide("R1", null));
    }

    @Test
    void completeRide() {
        Driver d = new Driver("D1", "John", loc1);
        service.registerDriver(d);

        Ride ride = service.requestRide("R1", loc1);

        service.completeRide(ride.getRideId());

        assertTrue(d.isAvailable());
        assertEquals(RideStatus.COMPLETED, ride.getStatus());
    }

    @Test
    void completeNonExistentRide() {
        assertThrows(RideNotFoundException.class, () -> service.completeRide("X"));
    }

    @Test
    void completeAlreadyCompletedRide() {
        Driver d = new Driver("D1", "John", loc1);
        service.registerDriver(d);

        Ride ride = service.requestRide("R1", loc1);
        service.completeRide(ride.getRideId());

        assertThrows(InvalidRideStateException.class, () -> service.completeRide(ride.getRideId()));
    }

    @Test
    void getNearestDrivers() {
        Driver d1 = new Driver("D1", "1", new Location(40.55, -74.60));
        Driver d2 = new Driver("D2", "2", new Location(41.35, -74.90));
        Driver d3 = new Driver("D3", "3", new Location(42.15, -74.85));
        Driver d4 = new Driver("D4", "4", new Location(39.80, -74.25));

        service.registerDriver(d1);
        service.registerDriver(d2);
        service.registerDriver(d3);
        service.registerDriver(d4);

        Location search = new Location(42.50, -74.50);

        List<Driver> list = service.getNearestDrivers(search, 4);

        assertEquals(4, list.size());
        assertEquals("D3", list.get(0).getDriverId());
        assertEquals("D2", list.get(1).getDriverId());
        assertEquals("D1", list.get(2).getDriverId());
        assertEquals("D4", list.get(3).getDriverId());
    }

    @Test
    void getNearestOnlyAvailable() {
        Driver d1 = new Driver("D1", "1", loc1);
        Driver d2 = new Driver("D2", "2", loc2);

        service.registerDriver(d1);
        service.registerDriver(d2);

        service.requestRide("R1", loc1);

        List<Driver> list = service.getNearestDrivers(loc1, 5);

        assertEquals(1, list.size());
        assertEquals("D2", list.get(0).getDriverId());
    }

    @Test
    void concurrentRideRequestsNoDoubleBooking() throws Exception {
        for (int i = 0; i < 10; i++) {
            service.registerDriver(new Driver("D" + i, "Driver" + i, loc1));
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(20);

        List<Ride> successfulRides = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger failures = new AtomicInteger();

        for (int i = 0; i < 20; i++) {
            int idx = i;
            executor.submit(() -> {
                try {
                    Ride ride = service.requestRide("R" + idx, loc1);
                    successfulRides.add(ride);
                } catch (NoAvailableDriverException | DriverAllocationException e) {
                    failures.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(10, successfulRides.size(), "Should allocate exactly 10 rides");
        assertEquals(10, failures.get(), "Should fail exactly 10 requests");

        long uniqueDrivers = successfulRides.stream()
                .map(r -> r.getDriver().getDriverId())
                .distinct()
                .count();

        assertEquals(10, uniqueDrivers, "Each driver should be allocated exactly once");
        assertEquals(0, service.getAvailableDrivers().size(), "All drivers should be busy");
    }

    @Test
    void concurrentRideCompletionsHandlesRaceConditions() throws Exception {
        List<Ride> rides = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Driver d = new Driver("D" + i, "Driver" + i, loc1);
            service.registerDriver(d);
            rides.add(service.requestRide("R" + i, loc1));
        }

        assertEquals(0, service.getAvailableDrivers().size());

        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);
        AtomicInteger successCount = new AtomicInteger(0);

        for (Ride ride : rides) {
            executor.submit(() -> {
                try {
                    service.completeRide(ride.getRideId());
                    successCount.incrementAndGet();
                } catch (InvalidRideStateException e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(5, successCount.get());
        assertEquals(5, service.getAvailableDrivers().size());
    }

    @Test
    void invalidNearestParams() {
        assertThrows(IllegalArgumentException.class, () -> service.getNearestDrivers(null, 5));

        assertThrows(IllegalArgumentException.class, () -> service.getNearestDrivers(loc1, 0));

        assertThrows(IllegalArgumentException.class, () -> service.getNearestDrivers(loc1, -1));
    }

    @Test
    void clearData() {
        Driver d = new Driver("D1","John",loc1);
        service.registerDriver(d);
        service.requestRide("R1", loc1);

        service.clear();

        assertEquals(0, service.getDriverCount());
        assertEquals(0, service.getRideCount());
    }
}