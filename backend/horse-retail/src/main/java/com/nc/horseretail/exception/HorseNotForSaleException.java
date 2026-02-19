package com.nc.horseretail.exception;

public class HorseNotForSaleException extends BusinessException {
    public HorseNotForSaleException() {
        super("Horse is not available for sale");
    }
}