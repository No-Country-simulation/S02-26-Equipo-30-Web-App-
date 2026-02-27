package com.nc.horseretail.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class MediaUploadRequest {

    private UUID horseId;

    @NotBlank
    private String mediaType;

    @NotBlank
    private String category;

    @NotBlank
    private String visibility;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate captureDate;

    private String context;

    private boolean unedited;
}