package com.nc.horseretail.controller;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.repository.HorseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final HorseRepository horseRepository;

    @PostMapping("/checkout")
    public Map<String,String> checkout(@RequestBody List<UUID> horseIds) throws Exception {

        List<SessionCreateParams.LineItem> items = new ArrayList<>();

        for(UUID id : horseIds){

            Horse horse = horseRepository.findById(id).orElseThrow();

            items.add(
                    SessionCreateParams.LineItem.builder()
                            //TODO set price id from stripe
//                            .setPrice(horse.getStripePriceId())
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