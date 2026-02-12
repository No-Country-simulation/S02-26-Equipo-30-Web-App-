package com.nc.horseretail.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ListingRequest {
    private UUID ownerId;
    private UUID horseId;
    private Double price;
}