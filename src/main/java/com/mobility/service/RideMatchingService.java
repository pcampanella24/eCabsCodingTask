package com.mobility.service;

import com.mobility.constants.RideMatchingCostants;
import com.mobility.enums.RideStatus;
import com.mobility.exception.*;
import com.mobility.model.Driver;
import com.mobility.model.Location;
import com.mobility.model.Ride;
import com.mobility.utils.IdGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RideMatchingService {

    private final ConcurrentHashMap<String, Driver> drivers;
    private final ConcurrentHashMap<String, Ride> rides;
    private final ReadWriteLock driverLock;

    public RideMatchingService() {
        this.drivers = new ConcurrentHashMap<>();
        this.rides = new ConcurrentHashMap<>();
        this.driverLock = new ReentrantReadWriteLock();
    }

    public void registerDriver(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }

        driverLock.writeLock().lock();
        try {
            drivers.put(driver.getDriverId(), driver);
        } finally {
            driverLock.writeLock().unlock();
        }
    }

    public void updateDriverLocation(String driverId, Location newLocation) {
        validateNotNull(driverId, "Driver ID");
        validateNotNull(newLocation, "Location");

        Driver driver = drivers.get(driverId);
        if (driver == null) {
            throw new DriverNotFoundException(driverId);
        }

        driver.setCurrentLocation(newLocation);
    }

    public Ride requestRide(String riderId, Location pickupLocation) {
        return requestRideWithRetry(riderId, pickupLocation, 0);
    }

    // Retry mechanism to handle race conditions during concurrent requests
    private Ride requestRideWithRetry(String riderId, Location pickupLocation, int attempt) {
        validateNotNull(riderId, "Rider ID");
        validateNotNull(pickupLocation, "Pickup location");

        if (attempt >= RideMatchingCostants.MAX_ALLOCATION_RETRIES) {
            throw new DriverAllocationException(attempt);
        }

        Driver nearestDriver = findNearestAvailableDriver(pickupLocation);
        if (nearestDriver == null) {
            throw new NoAvailableDriverException(pickupLocation);
        }

        // Atomic CAS operation ensures only one thread can allocate this driver
        if (nearestDriver.markAsUnavailable()) {
            String rideId = IdGenerator.generateRideId();
            Ride ride = new Ride(rideId, riderId, nearestDriver, pickupLocation);
            rides.put(rideId, ride);
            return ride;
        }

        // Driver was taken by another request, retry with next available driver
        return requestRideWithRetry(riderId, pickupLocation, attempt + 1);
    }

    public void completeRide(String rideId) {
        validateNotNull(rideId, "Ride ID");

        Ride ride = rides.get(rideId);
        if (ride == null) {
            throw new RideNotFoundException(rideId);
        }

        synchronized (ride) {
            RideStatus currentStatus = ride.getStatus();

            if (currentStatus == RideStatus.COMPLETED || currentStatus == RideStatus.CANCELLED) {
                throw new InvalidRideStateException(rideId, currentStatus, "complete");
            }

            ride.markAsCompleted();
            ride.getDriver().markAsAvailable();
        }
    }

    public List<Driver> getNearestDrivers(Location location, int count) {
        validateNotNull(location, "Location");
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }

        driverLock.readLock().lock();
        try {
            return drivers.values().stream()
                    .filter(Driver::isAvailable)
                    .sorted(Comparator.comparingDouble(d -> d.getCurrentLocation().distanceTo(location)))
                    .limit(count)
                    .collect(Collectors.toList());
        } finally {
            driverLock.readLock().unlock();
        }
    }

    public List<Driver> getAvailableDrivers() {
        driverLock.readLock().lock();
        try {
            return drivers.values().stream()
                    .filter(Driver::isAvailable)
                    .collect(Collectors.toList());
        } finally {
            driverLock.readLock().unlock();
        }
    }

    private Driver findNearestAvailableDriver(Location location) {
        driverLock.readLock().lock();
        try {
            return drivers.values().stream()
                    .filter(Driver::isAvailable)
                    .min(Comparator.comparingDouble(d -> d.getCurrentLocation().distanceTo(location)))
                    .orElse(null);
        } finally {
            driverLock.readLock().unlock();
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    // Utility methods for testing and metrics

    public Driver getDriver(String driverId) {
        return drivers.get(driverId);
    }

    public int getDriverCount() {
        return drivers.size();
    }

    public int getRideCount() {
        return rides.size();
    }

    public void clear() {
        driverLock.writeLock().lock();
        try {
            drivers.clear();
            rides.clear();
            IdGenerator.reset();
        } finally {
            driverLock.writeLock().unlock();
        }
    }
}