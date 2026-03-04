package com.nc.horseretail.service;


import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.repository.HorseRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;



import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final HorseRepository horseRepository;
    private final ListingRepository listingRepository;

    public String createCheckoutSession(List<UUID> horseIds) throws Exception {

        //TODO Este método va????

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for(UUID id : horseIds) {

            Horse horse = horseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Horse not found"));

            List<Listing> listings = listingRepository
                    .findByHorseIdAndStatus(horse.getId(), ListingStatus.ACTIVE);

            if (listings.isEmpty()) {
                throw new RuntimeException("Active listing not found for horse: " + horse.getName());
            }

            Listing listing = listings.get(0);

            Long amount = Math.round(listing.getAskingPriceUsd() * 100);

            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setUnitAmount(amount)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(horse.getName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:3000/success")
                        .setCancelUrl("http://localhost:3000/cancel")
                        .addAllLineItem(lineItems)
                        .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
}