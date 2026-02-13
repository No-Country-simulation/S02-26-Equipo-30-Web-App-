package com.nc.horseretail.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ListingResponse {
    private UUID id;
    private UUID ownerId;
    private String ownerName;
    private Double price;
    private String status;
}