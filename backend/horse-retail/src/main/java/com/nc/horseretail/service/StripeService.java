package com.nc.horseretail.service;

import com.nc.horseretail.dto.stripe.CheckoutListingResponse;
import com.nc.horseretail.dto.stripe.CheckoutSessionResponse;
import com.nc.horseretail.dto.stripe.CreateCheckoutRequest;

import java.util.List;
import java.util.UUID;

public interface StripeService {

    List<CheckoutListingResponse> listAvailableListingsForCheckout();

    CheckoutSessionResponse createCheckoutSession(CreateCheckoutRequest request, UUID buyerId) throws Exception;
}
