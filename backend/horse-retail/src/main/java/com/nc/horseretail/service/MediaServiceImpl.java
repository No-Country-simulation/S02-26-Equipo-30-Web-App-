
package com.nc.horseretail.service;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.media.MediaAsset;
import com.nc.horseretail.repository.MediaAssetRepository;
import com.nc.horseretail.repository.HorseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaAssetRepository mediaAssetRepository;
    private final HorseRepository horseRepository;
    
    @Override
    public MediaResponse uploadMedia(MediaUploadRequest request) {
        
        Horse horse = horseREpository.findById(request.getHorseId())
                .orElseThrow(() -> new RuntimeException("Horse not found"));
        
        MediaAsset media = MediaAsset.builder()
                .horse(horse)
                .url(request.getUrl())
                .mediaType(request.getMediaType())
                .captureDate(request.getCaptureDate())
                .context(request.getContext())
                .unedited(request.isUnedited())
                .build();
        
        MediaAsset saved = mediaAssetRepository.save(media);

        return mapToResponse(saved);
    }
    
    @Override
    public List<MediaResponse> getMediaByHorse(UUID horseId) {
        return mediaAssetRepository.findByHorseId(horseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MediaResponse mapToResponse(MediaAsset media) {
        return MediaResponse.builder()
                .id(media.getId())
                .url(media.getUrl())
                .mediaType(media.getMediaType())
                .captureDate(media.getCaptureDate())
                .context(media.getContext())
                .unedited(media.isUnedited())
                .build();
    }
}
