package com.nc.horseretail.config;

import com.nc.horseretail.repository.MediaAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@SuppressWarnings("unused") // Used in @PreAuthorize annotations in MediaController
@Component("mediaSecurity")
@RequiredArgsConstructor
public class MediaSecurity {

    private final MediaAssetRepository mediaRepository;

    public boolean isOwner(UUID mediaId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof SecurityUser securityUser)) {
            return false;
        }

        UUID currentUserId = securityUser.getDomainUser().getId();

        return mediaRepository.findById(mediaId)
                .map(media -> media.getUploadedBy()
                        .getId().equals(currentUserId)).orElse(false);
    }
}