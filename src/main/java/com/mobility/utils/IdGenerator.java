package com.mobility.utils;

import com.mobility.constants.RideMatchingCostants;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final AtomicLong counter = new AtomicLong(0);

    private IdGenerator() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static String generateRideId() {
        return RideMatchingCostants.RIDE_ID_PREFIX + counter.incrementAndGet();
    }

    public static void reset() {
        counter.set(0);
    }
}