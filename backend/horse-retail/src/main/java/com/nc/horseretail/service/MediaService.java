
package com.nc.horseretail.service;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface MediaService {

    MediaResponse uploadMedia(
        MultipartFile file,
        UUID horseId,
        String mediaType,
        String captureDate,
        String context,
        boolean unedited
);

    List<MediaResponse> getMediaByHorse(UUID horseId);
}
