package com.nc.horseretail.service;

import com.nc.horseretail.dto.stripe.CheckoutListingResponse;
import com.nc.horseretail.dto.stripe.CheckoutSessionResponse;
import com.nc.horseretail.dto.stripe.CreateCheckoutRequest;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ExternalServiceException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.repository.ListingRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    // Stripe supports up to 8 digits in the smallest currency unit for USD.
    private static final long MAX_USD_AMOUNT_IN_CENTS = 99_999_999L;

    private final ListingRepository listingRepository;

    @Value("${app.checkout.success-url:http://localhost:3000/success}")
    private String successUrl;

    @Value("${app.checkout.cancel-url:http://localhost:3000/cancel}")
    private String cancelUrl;

    @Override
    public List<CheckoutListingResponse> listAvailableListingsForCheckout() {
        return listingRepository
                .findByStatus(ListingStatus.ACTIVE, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toCheckoutListing)
                .toList();
    }

    @Override
    public CheckoutSessionResponse createCheckoutSession(CreateCheckoutRequest request, UUID buyerId) throws Exception {
        if (Stripe.apiKey == null || Stripe.apiKey.isBlank()) {
            throw new ExternalServiceException("Stripe secret key is not configured");
        }

        List<SessionCreateParams.LineItem> lineItems = request.getListingIds()
                .stream()
                .distinct()
                .map(listingId -> buildLineItem(listingId, buyerId))
                .toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .build();

        try {
            Session session = Session.create(params);
            return CheckoutSessionResponse.builder()
                    .url(session.getUrl())
                    .build();
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to create Stripe checkout session");
        }
    }

    private SessionCreateParams.LineItem buildLineItem(UUID listingId, UUID buyerId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Listing is not active: " + listingId);
        }

        if (listing.getOwner().getId().equals(buyerId)) {
            throw new BusinessException("You cannot create a checkout for your own listing");
        }

        Double askingPriceUsd = listing.getAskingPriceUsd();
        if (askingPriceUsd == null || askingPriceUsd <= 0) {
            throw new BusinessException("Invalid price for listing: " + listingId);
        }

        long priceInCents = Math.round(askingPriceUsd * 100);
        if (priceInCents > MAX_USD_AMOUNT_IN_CENTS) {
            throw new BusinessException("Listing price exceeds Stripe maximum supported amount for USD");
        }

        // Stripe line items are derived from the listing because that is where the current asking price lives.
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
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
                .build();
    }

    private CheckoutListingResponse toCheckoutListing(Listing listing) {
        return CheckoutListingResponse.builder()
                .listingId(listing.getId())
                .horseName(listing.getHorse().getName())
                .askingPriceUsd(listing.getAskingPriceUsd())
                .build();
    }
}
