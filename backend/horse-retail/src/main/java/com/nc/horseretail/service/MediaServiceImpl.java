package com.nc.horseretail.service;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.MediaMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.media.*;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.MediaAssetRepository;
import com.nc.horseretail.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaServiceImpl implements MediaService {

    private final MediaAssetRepository mediaRepository;
    private final HorseRepository horseRepository;
    private final CloudinaryService cloudinaryService;
    private final MediaMapper mediaMapper;
    private final UserRepository userRepository;

    // ==========================================================
    // UPLOAD MEDIA
    // ==========================================================

    @Override
    public MediaResponse uploadMedia(MultipartFile file, MediaUploadRequest request, UUID currentUserId) {

        validateFile(file);

        User user = userRepository.findById(currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("User not found"));

        Horse horse = horseRepository.findById(request.getHorseId()).orElseThrow(
                () -> new EntityNotFoundException("Horse not found"));

        CloudinaryUploadResult uploadResult = cloudinaryService.upload(file, "horses");

        MediaAsset media = MediaAsset.builder()
                .url(uploadResult.getUrl())
                .publicId(uploadResult.getPublicId())
                .mediaType(MediaType.valueOf(request.getMediaType()))
                .category(MediaCategory.valueOf(request.getCategory()))
                .visibility(MediaVisibility.valueOf(request.getVisibility()))
                .horse(horse)
                .uploadedBy(user)
                .captureDate(request.getCaptureDate())
                .context(request.getContext())
                .unedited(request.isUnedited())
                .build();

        MediaAsset saved = mediaRepository.save(media);

        return mediaMapper.toResponse(saved);
    }

    // ==========================================================
    // DELETE MEDIA
    // ==========================================================

    @Override
    public void deleteMedia(UUID mediaId) {

        MediaAsset media = mediaRepository.findById(mediaId).orElseThrow(
                () -> new EntityNotFoundException("Media not found"));

        cloudinaryService.delete(media.getPublicId());

        mediaRepository.delete(media);
    }

    // ==========================================================
    // GET BY ID
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public MediaResponse getMedia(UUID mediaId) {

        MediaAsset media = mediaRepository.findById(mediaId).orElseThrow(
                () -> new EntityNotFoundException("Media not found"));

        return mediaMapper.toResponse(media);
    }

    // ==========================================================
    // GET BY HORSE (PUBLIC ONLY)
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public List<MediaResponse> getMediaByHorse(UUID horseId) {

        return mediaRepository.findByHorseId(horseId).stream()
                .filter(media -> media.getVisibility() == MediaVisibility.PUBLIC)
                .map(mediaMapper::toResponse).toList();
    }

    // ==========================================================
    // GET BY LISTING
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public List<MediaResponse> getMediaByListing(UUID listingId) {

        List<MediaAsset> mediaList = mediaRepository.findByListingId(listingId);

        return mediaList.stream().filter(media -> media.getVisibility() == MediaVisibility.PUBLIC)
                .map(mediaMapper::toResponse).toList();
    }


    // ==========================================================
    // PRIVATE HELPERS
    // ==========================================================

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        long maxSize = 10 * 1024 * 1024L; // 10MB

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File exceeds maximum size of 10MB");
        }
    }
}