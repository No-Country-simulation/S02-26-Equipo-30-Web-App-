package com.nc.horseretail.service;

import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import com.nc.horseretail.dto.horse.HorseCreateResponse;
import com.nc.horseretail.dto.horse.HorseRequest;
import com.nc.horseretail.dto.horse.HorseResponse;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.HorseMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.horse.HorseStatus;
import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.model.media.MediaCategory;
import com.nc.horseretail.model.media.MediaType;
import com.nc.horseretail.model.media.MediaVisibility;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final HorseMapper horseMapper;
    private final ArithmeticTrustScoreService arithmeticTrustScoreService;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final ListingService listingService;
    private final MediaService mediaService;

    @Override
    @Transactional
    public HorseCreateResponse createHorse(HorseRequest request, UUID userId) {
        User owner = getUserOrThrow(userId);
        Horse horse = persistHorse(request, owner);
        return HorseCreateResponse.builder()
                .horseId(horse.getId())
                .build();
    }

    @Override
    @Transactional
    public HorseCreateResponse createHorse(HorseRequest request,
                                           UUID userId,
                                           Double price,
                                           List<MultipartFile> mediaFiles,
                                           List<MultipartFile> documentFiles) {
        User owner = getUserOrThrow(userId);
        Horse horse = persistHorse(request, owner);
        UUID listingId = null;

        if (price != null) {
            if (price <= 0) {
                throw new BusinessException("Price must be greater than zero");
            }
            ListingResponse listingResponse = createListing(horse, price, userId);
            listingId = listingResponse.getId();
        }

        List<UUID> mediaIds = uploadMediaFiles(horse, mediaFiles, userId);
        List<UUID> documentIds = uploadDocumentFiles(horse, documentFiles, userId);

        return HorseCreateResponse.builder()
                .horseId(horse.getId())
                .listingId(listingId)
                .mediaIds(mediaIds)
                .documentIds(documentIds)
                .build();
    }

    @Override
    public Page<HorseResponse> getHorses(String keyword, MainUse mainUse, Pageable pageable) {
        String sanitizedKeyword = keyword == null ? "" : keyword.trim();
        if (sanitizedKeyword.isBlank()) {
            sanitizedKeyword = "";
        }
        return horseRepository.search(sanitizedKeyword, mainUse, pageable).map(horseMapper::toDto);
    }

    @Override
    public HorseResponse getHorseById(UUID id) {
        Horse horse = findByIdOrThrow(id);
        return horseMapper.toDto(horse);
    }

    @Override
    public HorseResponse updateHorse(UUID id, HorseRequest request, UUID userId) {
        Horse horse = findByIdOrThrow(id);
        if (!horse.getOwner().getId().equals(userId)) {
            log.warn("User attempted to update horse {} owned by another user", id);
            throw new ResourceNotFoundException("Horse not found with id: " + id);
        }
        horseMapper.updateEntityFromDto(request, horse);
        arithmeticTrustScoreService.applyTrustScore(horse);
        Horse savedHorse = horseRepository.save(horse);
        return horseMapper.toDto(savedHorse);
    }

    @Override
    @Transactional
    public void deleteHorse(UUID id, UUID userId) {
        Horse horse = findByIdOrThrow(id);

        if (!horse.getOwner().getId().equals(userId)) {
            log.warn("User {} attempted to delete horse {} owned by another user", userId, id);
            throw new ResourceNotFoundException("Horse not found with id: " + id);
        }

        if (horse.getStatus() == HorseStatus.DELETED) {
            throw new BusinessException("Horse already deleted");
        }

        List<Listing> activeListings = listingRepository.findByHorseIdAndStatus(id, ListingStatus.ACTIVE);
        activeListings.forEach(listing -> listing.setStatus(ListingStatus.CANCELLED));

        horse.setStatus(HorseStatus.DELETED);

        log.info("Horse {} soft deleted by owner {}", id, userId);
    }

    @Override
    public List<HorseResponse> getMyHorses(UUID userId) {
        User user = getUserOrThrow(userId);
        return horseRepository.findAllByOwner(user)
                .stream()
                .map(horseMapper::toDto)
                .toList();
    }

    @Override
    public Long countMyHorses(UUID userId) {
        User user = getUserOrThrow(userId);
        return horseRepository.countHorsesByOwner(user);
    }

    @Override
    public long countTotalHorses() {
        return horseRepository.count();
    }

    @Override
    @Transactional
    public void deleteHorseByAdmin(UUID horseId) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + horseId));

        if (horse.getStatus() == HorseStatus.DELETED) {
            throw new BusinessException("Horse is already deleted");
        }

        List<Listing> activeListings = listingRepository.findByHorseIdAndStatus(horseId, ListingStatus.ACTIVE);
        for (Listing listing : activeListings) {
            listing.setStatus(ListingStatus.CANCELLED);
        }

        horse.setStatus(HorseStatus.DELETED);
        horseRepository.save(horse);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Horse findByIdOrThrow(UUID id) {
        return horseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Horse not found with id: " + id));
    }

    private Horse persistHorse(HorseRequest request, User owner) {
        log.info("Creating new horse for user {}", owner.getUsername());

        Horse horse = horseMapper.toEntity(request);
        horse.setOwner(owner);
        horse.setStatus(HorseStatus.ACTIVE);
        if (horse.getSellerVerified() == null) {
            horse.setSellerVerified(owner.isEmailVerified());
        }
        arithmeticTrustScoreService.applyTrustScore(horse);
        return horseRepository.save(horse);
    }

    private ListingResponse createListing(Horse horse, Double price, UUID userId) {
        ListingRequest listingRequest = new ListingRequest();
        listingRequest.setHorseId(horse.getId());
        listingRequest.setPrice(price);
        return listingService.createListing(listingRequest, userId);
    }

    private List<UUID> uploadMediaFiles(Horse horse, List<MultipartFile> files, UUID userId) {
        List<UUID> uploadedIds = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return uploadedIds;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            MediaUploadRequest request = new MediaUploadRequest();
            request.setHorseId(horse.getId());
            request.setMediaType(resolveMediaType(file).name());
            request.setCategory(MediaCategory.HORSE_IMAGE.name());
            request.setVisibility(MediaVisibility.PUBLIC.name());
            request.setContext("Uploaded during horse creation");
            request.setUnedited(false);
            MediaResponse response = mediaService.uploadMedia(file, request, userId);
            uploadedIds.add(response.getId());
        }

        return uploadedIds;
    }

    private List<UUID> uploadDocumentFiles(Horse horse, List<MultipartFile> files, UUID userId) {
        List<UUID> uploadedIds = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return uploadedIds;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            MediaUploadRequest request = new MediaUploadRequest();
            request.setHorseId(horse.getId());
            request.setMediaType(MediaType.DOCUMENT.name());
            request.setCategory(MediaCategory.CERTIFICATE.name());
            request.setVisibility(MediaVisibility.PRIVATE.name());
            request.setContext("Veterinary document uploaded during horse creation");
            request.setUnedited(true);
            MediaResponse response = mediaService.uploadMedia(file, request, userId);
            uploadedIds.add(response.getId());
        }

        return uploadedIds;
    }

    private MediaType resolveMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return MediaType.IMAGE;
            }
            if (contentType.startsWith("video/")) {
                return MediaType.VIDEO;
            }
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String normalized = filename.toLowerCase(Locale.ROOT);
            if (normalized.endsWith(".jpg") || normalized.endsWith(".jpeg")
                    || normalized.endsWith(".png") || normalized.endsWith(".gif")
                    || normalized.endsWith(".webp")) {
                return MediaType.IMAGE;
            }
            if (normalized.endsWith(".mp4") || normalized.endsWith(".mov")
                    || normalized.endsWith(".avi") || normalized.endsWith(".webm")) {
                return MediaType.VIDEO;
            }
        }

        throw new BusinessException("Unsupported media file type");
    }
}
