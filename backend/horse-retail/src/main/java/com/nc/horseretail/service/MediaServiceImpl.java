
package com.nc.horseretail.service;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.media.MediaAsset;
import com.nc.horseretail.repository.MediaAssetRepository;
import com.nc.horseretail.repository.HorseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import com.nc.horseretail.model.media.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaAssetRepository mediaAssetRepository;
    private final HorseRepository horseRepository;
    private final CloudinaryService cloudinaryService;
    
    @Override
    public MediaResponse uploadMedia(
        MultipartFile file,
        UUID horseId,
        String mediaType,
        String captureDate,
        String context,
        boolean unedited
) {

    Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Horse not found"));

    String fileUrl = cloudinaryService.uploadFile(file);

    MediaAsset media = MediaAsset.builder()
            .horse(horse)
            .url(fileUrl)
            .mediaType(Enum.valueOf(MediaType.class, mediaType))
            .captureDate(LocalDate.parse(captureDate))
            .context(context)
            .unedited(unedited)
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
