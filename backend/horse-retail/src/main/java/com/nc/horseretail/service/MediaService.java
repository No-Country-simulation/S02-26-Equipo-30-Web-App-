
package com.nc.horseretail.service;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;

import java.util.List;
import java.util.UUID;


public interface MediaService {

    MediaResponse uploadMedia(MediaUploadRequest request);

    List<MediaResponse> getMediaByHorse(UUID horseId);
}
