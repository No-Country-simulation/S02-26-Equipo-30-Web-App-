package com.nc.horseretail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChatRequest {
    @NotNull
    private UUID listingId;
    @NotBlank
    @Size(min = 1, max = 2000)
    private String text;
}
