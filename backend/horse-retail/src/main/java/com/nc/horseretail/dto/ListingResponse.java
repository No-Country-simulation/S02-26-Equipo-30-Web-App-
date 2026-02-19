package com.nc.horseretail.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ListingResponse {
    private UUID id;
    private UUID ownerId;
    private String ownerName;
    private Double price;
    private String status;
}