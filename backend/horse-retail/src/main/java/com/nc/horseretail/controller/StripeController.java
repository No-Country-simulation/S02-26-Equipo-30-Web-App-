package com.nc.horseretail.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final ListingRepository listingRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    // =========================
    //  ENDPOINT AUXILIAR PARA POSTMAN
    // =========================
    @GetMapping("/checkout/listings")
    public List<Map<String, Object>> listForCheckout() {

        return listingRepository
                .findByStatus(ListingStatus.ACTIVE, Pageable.unpaged())
                .getContent()
                .stream()
                .map(listing -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("UUID", listing.getId());
                    map.put("horseName", listing.getHorse().getName());
                    map.put("askingPriceUsd", listing.getAskingPriceUsd());
                    return map;
                })
                .toList();
    }

    // =========================
    //  CHECKOUT CORREGIDO
    // =========================
    @PostMapping("/checkout")
    public Map<String,String> checkout(@RequestBody List<UUID> listingIds) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        List<SessionCreateParams.LineItem> items = new ArrayList<>();

        for(UUID id : listingIds){

            Listing listing = listingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Listing not found: " + id));

            if (listing.getAskingPriceUsd() == null || listing.getAskingPriceUsd() <= 0) {
                throw new RuntimeException("Invalid price for listing: " + id);
            }

            long priceInCents = Math.round(listing.getAskingPriceUsd() * 100);

            items.add(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setUnitAmount(priceInCents)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(listing.getHorse().getName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .setQuantity(1L)
                            .build()
            );
        }

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:3000/success")
                        .setCancelUrl("http://localhost:3000/cancel")
                        .addAllLineItem(items)
                        .build();

        Session session = Session.create(params);

        Map<String,String> response = new HashMap<>();
        response.put("url", session.getUrl());

        return response;
    }
}