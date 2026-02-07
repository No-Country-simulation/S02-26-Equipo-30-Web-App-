package com.nc.horseretail.exception;

public class ListingNotActiveException extends BusinessException {
    public ListingNotActiveException() {
        super("Listing is not active");
    }
}