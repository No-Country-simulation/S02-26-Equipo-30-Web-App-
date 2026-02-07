package com.nc.horseretail.model.horse;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {
    private String country;
    private String region;
    private String city;
}