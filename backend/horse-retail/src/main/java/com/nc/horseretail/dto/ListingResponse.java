package com.nc.horseretail.dto;

import com.nc.horseretail.dto.horse.HorseResponse;
import lombok.Builder;
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

    private HorseResponse horse;
}