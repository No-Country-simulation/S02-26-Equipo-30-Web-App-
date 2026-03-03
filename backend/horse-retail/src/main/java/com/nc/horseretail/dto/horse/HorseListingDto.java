package com.nc.horseretail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class HorseListingDto {
    private UUID id;
    private String name;
    private Double askingPriceUsd;
}