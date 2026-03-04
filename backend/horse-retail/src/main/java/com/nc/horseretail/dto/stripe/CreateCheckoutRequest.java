package com.nc.horseretail.dto.stripe;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateCheckoutRequest {

    // The checkout is built from listings because the price belongs to the listing, not the horse.
    @NotEmpty
    private List<@NotNull UUID> listingIds;
}
