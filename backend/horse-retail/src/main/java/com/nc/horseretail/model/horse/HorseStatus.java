package com.nc.horseretail.model.horse;

public enum HorseStatus {

    ACTIVE,
    INACTIVE,
    SOLD,
    DELETED;

    public boolean isVisible() {
        return this == ACTIVE;
    }

    public boolean canBeListed() {
        return this == ACTIVE;
    }
}