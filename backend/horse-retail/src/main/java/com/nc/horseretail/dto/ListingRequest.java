package com.nc.horseretail.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ListingRequest {

    @NotNull
    private UUID horseId;

    @NotNull
    @Positive
    private Double price;
}