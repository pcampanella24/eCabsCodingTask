package com.mobility;

import com.mobility.service.RideMatchingService;
import com.mobility.model.*;

import java.util.Random;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== Ride Matching Service Demo ===\n");

        System.out.println("PART 1: Fixed Coordinates\n");

        RideMatchingService service = new RideMatchingService();

        Driver d1 = new Driver("D1", "John", new Location(40.75, -74.00));
        Driver d2 = new Driver("D2", "Jane", new Location(40.71, -74.01));
        Driver d3 = new Driver("D3", "Bob", new Location(40.78, -73.98));

        service.registerDriver(d1);
        service.registerDriver(d2);
        service.registerDriver(d3);

        System.out.println("Registered drivers:");

        for (Driver driver : service.getAvailableDrivers()) {
            Location loc = driver.getCurrentLocation();
            System.out.printf("  %s (%s): (%.2f, %.2f)%n", driver.getDriverId(), driver.getName(), loc.getLatitude(), loc.getLongitude());
        }

        System.out.println();

        Location pickup = new Location(40.76, -73.99);
        System.out.println("Ride request from: " + pickup);

        Ride ride = service.requestRide("RIDER1", pickup);
        double distance = ride.getDriver().getCurrentLocation().distanceTo(pickup);

        System.out.println("\nRide allocated:");

        System.out.printf("  Details: %s (%s) - Distance: %.2f%n", ride.getRideId(), ride.getDriver().getName(), distance);
        System.out.println();

        System.out.println("Available: " + service.getAvailableDrivers().size() + "/3");

        service.completeRide(ride.getRideId());

        System.out.println("Ride completed");
        System.out.println("Available: " + service.getAvailableDrivers().size() + "/3\n");

        System.out.println("=".repeat(50) + "\n");
        System.out.println("PART 2: Random Coordinates\n");

        service.clear();
        Random random = new Random(45);

        System.out.println("Registering 10 drivers:");

        for (int i = 1; i <= 10; i++) {

            double lat = 40.0 + random.nextDouble();
            double lon = -74.0 + random.nextDouble();

            Driver driver = new Driver("D" + i, "Driver" + i, new Location(lat, lon));

            service.registerDriver(driver);

            System.out.printf("  %s (%s): (%.2f, %.2f)%n", driver.getDriverId(), driver.getName(), lat, lon);
        }

        System.out.println();

        System.out.println("Requesting 5 rides:\n");

        for (int i = 1; i <= 5; i++) {

            double lat = 40.0 + random.nextDouble();
            double lon = -74.0 + random.nextDouble();

            Location ridePickup = new Location(lat, lon);

            System.out.printf("Ride %d pickup: (%.2f, %.2f)%n", i, lat, lon);

            Ride rideRequest = service.requestRide("RIDER" + i, ridePickup);

            Driver allocated = rideRequest.getDriver();

            double dist = allocated.getCurrentLocation().distanceTo(ridePickup);

            System.out.printf("  Details: %s (%s) - Distance: %.2f%n", rideRequest.getRideId(), allocated.getName(), dist);
            System.out.println();
        }

        System.out.println("Final state:");
        System.out.println("  Total drivers: 10");
        System.out.println("  Available: " + service.getAvailableDrivers().size());
        System.out.println("  In ride: " + (10 - service.getAvailableDrivers().size()));

        System.out.println("\n=== Demo Complete ===");
    }
}