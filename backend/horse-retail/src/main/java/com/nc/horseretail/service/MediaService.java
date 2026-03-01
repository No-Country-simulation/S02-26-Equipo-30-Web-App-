package com.nc.horseretail.service;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaService {


    MediaResponse uploadMedia(MultipartFile file, MediaUploadRequest request, UUID currentUserId);

    void deleteMedia(UUID mediaId);

    @Transactional(readOnly = true)
    MediaResponse getMedia(UUID mediaId);

    @Transactional(readOnly = true)
    List<MediaResponse> getMediaByHorse(UUID horseId);

    List<MediaResponse> getMediaByListing(UUID listingId);
}
