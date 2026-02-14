

package com.nc.horseretail.dto;
import com.nc.horseretail.model.media.MediaType;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class MediaUploadRequest {

    private UUID horseId;
    private String url;
    private MediaType mediaType;
    private LocalDate captureDate;
    private String context;
    private boolean unedited;
}
