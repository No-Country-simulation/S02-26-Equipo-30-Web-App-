package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import com.nc.horseretail.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    // ==========================================================
    // UPLOAD MEDIA (AUTH REQUIRED)
    // ==========================================================

    @Operation(
            summary = "Upload media file",
            description = "Uploads an image or document associated with a horse or listing. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media uploaded successfully",
                    content = @Content(schema = @Schema(implementation = MediaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> uploadMedia(

            @Parameter(description = "File to upload (image or document)", required = true)
            @RequestPart("file") MultipartFile file,

            @Valid
            @ModelAttribute MediaUploadRequest request,

            @AuthenticationPrincipal SecurityUser user
    ) {

        MediaResponse response = mediaService.uploadMedia(
                file,
                request,
                user.getId()
        );

        return ResponseEntity.ok(response);
    }


    // ==========================================================
    // DELETE MEDIA (OWNER OR ADMIN)
    // ==========================================================

    @Operation(
            summary = "Delete media",
            description = "Deletes a media file. Only the owner or an ADMIN can delete it."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Media deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @DeleteMapping("/{mediaId}")
    @PreAuthorize("hasRole('ADMIN') or @mediaSecurity.isOwner(#mediaId, authentication)")
    public ResponseEntity<Void> deleteMedia(

            @Parameter(description = "Media ID", required = true)
            @PathVariable UUID mediaId
    ) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.noContent().build();
    }


    // ==========================================================
    // GET MEDIA BY ID (PUBLIC)
    // ==========================================================

    @Operation(
            summary = "Get media by ID",
            description = "Returns a single media file metadata by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media found",
                    content = @Content(schema = @Schema(implementation = MediaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @GetMapping("/{mediaId}")
    public ResponseEntity<MediaResponse> getMedia(

            @Parameter(description = "Media ID", required = true)
            @PathVariable UUID mediaId
    ) {
        return ResponseEntity.ok(mediaService.getMedia(mediaId));
    }


    // ==========================================================
    // GET MEDIA BY HORSE (PUBLIC)
    // ==========================================================

    @Operation(
            summary = "Get media by horse",
            description = "Returns all public media associated with a specific horse."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media list retrieved successfully")
    })
    @GetMapping("/horse/{horseId}")
    public ResponseEntity<List<MediaResponse>> getMediaByHorse(

            @Parameter(description = "Horse ID", required = true)
            @PathVariable UUID horseId
    ) {
        return ResponseEntity.ok(mediaService.getMediaByHorse(horseId));
    }


    // ==========================================================
    // GET MEDIA BY LISTING (PUBLIC)
    // ==========================================================

    @Operation(
            summary = "Get media by listing",
            description = "Returns all public media associated with a listing."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media list retrieved successfully")
    })
    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<MediaResponse>> getMediaByListing(

            @Parameter(description = "Listing ID", required = true)
            @PathVariable UUID listingId
    ) {
        return ResponseEntity.ok(mediaService.getMediaByListing(listingId));
    }
}