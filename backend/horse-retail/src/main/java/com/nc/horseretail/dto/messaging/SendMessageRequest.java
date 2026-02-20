package com.nc.horseretail.dto.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SendMessageRequest {

    @NotNull
    private UUID listingId;

    @NotBlank
    @Size(max = 2000)
    private String text;
}
