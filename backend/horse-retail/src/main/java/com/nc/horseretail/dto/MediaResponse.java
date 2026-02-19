
package com.nc.horseretail.dto;

import com.nc.horseretail.model.media.MediaType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MediaResponse {
    
    private UUID id;
    private String url;
    private MediaType mediaType;
    private LocalDate captureDate;
    private String context;
    private boolean unedited;

}
