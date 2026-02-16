package com.nc.horseretail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SendMessageRequest {

    @NotNull
    private UUID listingId;

    @NotBlank
    @Size(max = 2000)
    private String text;
}
