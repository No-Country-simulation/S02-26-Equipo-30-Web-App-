package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.stripe.CheckoutListingResponse;
import com.nc.horseretail.dto.stripe.CheckoutSessionResponse;
import com.nc.horseretail.dto.stripe.CreateCheckoutRequest;
import com.nc.horseretail.service.StripeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    // =========================
    //  AUXILIARY CHECKOUT LIST
    // =========================
    @GetMapping("/checkout/listings")
    public List<CheckoutListingResponse> listForCheckout() {
        return stripeService.listAvailableListingsForCheckout();
    }

    // =========================
    //  CHECKOUT
    // =========================
    @PostMapping("/checkout")
    public CheckoutSessionResponse checkout(
            @Valid @RequestBody CreateCheckoutRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) throws Exception {
        return stripeService.createCheckoutSession(request, securityUser.getId());
    }
}
