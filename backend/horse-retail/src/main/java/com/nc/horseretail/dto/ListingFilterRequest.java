package com.nc.horseretail.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ListingFilterRequest {

    private String keyword;

    private String discipline;

    private String breed;

    private String location;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;
}