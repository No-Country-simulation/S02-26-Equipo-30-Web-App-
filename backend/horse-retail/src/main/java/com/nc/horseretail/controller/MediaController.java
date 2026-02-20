package com.nc.horseretail.controller;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import com.nc.horseretail.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

//    @PostMapping
    public ResponseEntity<MediaResponse> uploadMedia(
            @RequestBody MediaUploadRequest request) {
        return ResponseEntity.ok(mediaService.uploadMedia(request));
    }

//    @GetMapping("/horse/{horseId}")
    public ResponseEntity<List<MediaResponse>> getMediaByHorse(
            @PathVariable UUID horseId) {
        return ResponseEntity.ok(mediaService.getMediaByHorse(horseId));
    }

    //TODO POST /upload  → AUTH
    //TODO DELETE /{fileId}  → AUTH (owner or ADMIN)
    //TODO GET /{fileId}  → PUBLIC
    //TODO GET /listing/{listingId}  → PUBLIC
    //TODO GET /horse/{horseId}  → PUBLIC
}
