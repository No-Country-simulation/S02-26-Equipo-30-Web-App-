package com.nc.horseretail.controller;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.dto.MediaUploadRequest;
import com.nc.horseretail.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(consumes = "multipart/form-data")
public ResponseEntity<MediaResponse> uploadMedia(
        @RequestParam("file") MultipartFile file,
        @RequestParam("horseId") UUID horseId,
        @RequestParam("mediaType") String mediaType,
        @RequestParam("captureDate") String captureDate,
        @RequestParam("context") String context,
        @RequestParam("unedited") boolean unedited
) {
    return ResponseEntity.ok(
            mediaService.uploadMedia(
                    file,
                    horseId,
                    mediaType,
                    captureDate,
                    context,
                    unedited
            )
    );
}

    @GetMapping("/horse/{horseId}")
    public ResponseEntity<List<MediaResponse>> getMediaByHorse(
            @PathVariable UUID horseId) {
        return ResponseEntity.ok(mediaService.getMediaByHorse(horseId));
    }
}
