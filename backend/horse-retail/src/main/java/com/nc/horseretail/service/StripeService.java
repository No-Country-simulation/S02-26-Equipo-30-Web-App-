package com.nc.horseretail.service;


import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.repository.HorseRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final HorseRepository horseRepository;

    public String createCheckoutSession(List<UUID> horseIds) throws Exception {

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for(UUID id : horseIds) {

            Horse horse = horseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Horse not found"));

            // TODO set listing price instead of hardcoded price id
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setPrice("horse.getStripePriceId()")
                            .setQuantity(1L)
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

        return session.getId();
    }
}