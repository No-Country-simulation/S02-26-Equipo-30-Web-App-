package com.nc.horseretail.dto.stripe;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckoutSessionResponse {

    private String url;
}
