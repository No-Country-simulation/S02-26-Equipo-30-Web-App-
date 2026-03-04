package com.nc.horseretail.dto.stripe;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CheckoutListingResponse {

    private UUID listingId;
    private String horseName;
    private Double askingPriceUsd;
}
