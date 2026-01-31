package com.mobility.exception;

public class DriverAllocationException extends RideMatchingException {

    private final int retryAttempts;

    public DriverAllocationException(int retryAttempts) {
        super("Driver allocation failed after " + retryAttempts + " retry attempts");
        this.retryAttempts = retryAttempts;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }
}