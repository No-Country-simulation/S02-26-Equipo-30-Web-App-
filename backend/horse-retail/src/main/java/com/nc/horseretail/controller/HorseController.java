package com.nc.horseretail.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.horse.HorseCreateResponse;
import com.nc.horseretail.dto.horse.HorseRequest;
import com.nc.horseretail.dto.horse.HorseResponse;
import com.nc.horseretail.exception.InvalidRequestException;
import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.service.HorseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Horse", description = "API for managing horses")
@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
@Slf4j
public class HorseController {

    private final HorseService horseService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    // ============================
    // CREATE HORSE
    // ============================
    @Operation(summary = "Create a new horse", description = "Creates a new horse owned by the authenticated user")
    @ApiResponse(responseCode = "201", description = "Horse created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HorseCreateResponse> createHorse(@Valid @RequestBody HorseRequest request,
                                                           @AuthenticationPrincipal SecurityUser securityUser) {
        log.info("Creating horse for user {}", securityUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(horseService.createHorse(request, securityUser.getId()));
    }

    @Operation(summary = "Create a new horse with optional listing and media",
            description = "Creates a new horse and, when provided, also creates its listing and uploads media/documents")
    @ApiResponse(responseCode = "201", description = "Horse created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HorseCreateResponse> createHorseMultipart(
            @RequestPart("horse") MultipartFile horsePart,
            @RequestParam(required = false) Double price,
            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
            @RequestPart(value = "documentFiles", required = false) List<MultipartFile> documentFiles,
            @AuthenticationPrincipal SecurityUser securityUser) {
        HorseRequest request = parseAndValidateHorse(horsePart);
        log.info("Creating horse with optional listing/media for user {}", securityUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(horseService.createHorse(request, securityUser.getId(), price, mediaFiles, documentFiles));
    }

    // ============================
    // GET / SEARCH HORSES (PAGINATED)
    // ============================
    @Operation(summary = "Get or search horses",
            description = "Retrieves a paginated list of horses. If a keyword is provided, " +
                    "it filters horses by name, breed or main use. Otherwise, it returns all horses.")
    @ApiResponse(responseCode = "200", description = "Horses retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<HorseResponse>> getHorses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MainUse mainUse,
            Pageable pageable) {
        return ResponseEntity.ok(
                horseService.getHorses(keyword, mainUse, pageable)
        );
    }

    // ============================
    // GET HORSE BY ID
    // ============================
    @Operation(summary = "Get horse by ID",
            description = "Retrieves full details of a specific horse")
    @ApiResponse(responseCode = "200", description = "Horse retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Horse not found")
    @GetMapping("/{id}")
    public ResponseEntity<HorseResponse> getHorseById(@PathVariable UUID id) {
        log.info("Fetching horse {}", id);
        return ResponseEntity.ok(horseService.getHorseById(id));
    }

    // ============================
    // UPDATE HORSE
    // ============================
    @Operation(summary = "Update horse",
            description = "Updates a horse owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Horse updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Not owner of the horse")
    @ApiResponse(responseCode = "404", description = "Horse not found")
    @PatchMapping("/{id}")
    public ResponseEntity<HorseResponse> updateHorse(@PathVariable UUID id,
                                                     @RequestBody HorseRequest request,
                                                     @AuthenticationPrincipal SecurityUser securityUser) {
        log.info("Updating horse {} by user {}", id, securityUser.getUsername());
        return ResponseEntity.ok(
                horseService.updateHorse(id, request, securityUser.getId())
        );
    }

    // ============================
    // DELETE HORSE
    // ============================
    @Operation(summary = "Delete horse",
            description = "Deletes a horse owned by the authenticated user")
    @ApiResponse(responseCode = "204", description = "Horse deleted successfully")
    @ApiResponse(responseCode = "403", description = "Not owner of the horse")
    @ApiResponse(responseCode = "404", description = "Horse not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorse(@PathVariable UUID id,
                                            @AuthenticationPrincipal SecurityUser securityUser) {
        log.info("Deleting horse {} by user {}", id, securityUser.getUsername());
        horseService.deleteHorse(id, securityUser.getId());
        return ResponseEntity.noContent().build();
    }

    // ============================
    // GET MY HORSES
    // ============================
    @Operation(summary = "Get my horses",
            description = "Returns all horses owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Horses retrieved successfully")
    @GetMapping("/me")
    public ResponseEntity<List<HorseResponse>> getMyHorses(
            @AuthenticationPrincipal SecurityUser securityUser) {

        log.info("Fetching horses for user {}", securityUser.getUsername());
        return ResponseEntity.ok(
                horseService.getMyHorses(securityUser.getId())
        );
    }

    // ============================
    // COUNT MY HORSES
    // ============================
    @Operation(summary = "Count my horses",
            description = "Returns total number of horses owned by authenticated user")
    @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    @GetMapping("/me/count")
    public ResponseEntity<Long> countMyHorses(
            @AuthenticationPrincipal SecurityUser securityUser) {

        log.info("Counting horses for user {}", securityUser.getUsername());
        return ResponseEntity.ok(
                horseService.countMyHorses(securityUser.getId())
        );
    }

    private HorseRequest parseAndValidateHorse(MultipartFile horsePart) {
        if (horsePart == null || horsePart.isEmpty()) {
            throw new InvalidRequestException("horse part is required");
        }

        final HorseRequest request;
        try {
            request = objectMapper.readValue(horsePart.getBytes(), HorseRequest.class);
        } catch (JsonProcessingException ex) {
            throw new InvalidRequestException("horse part contains invalid JSON");
        } catch (Exception ex) {
            throw new InvalidRequestException("horse part could not be processed");
        }

        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(this::formatViolation)
                    .collect(Collectors.joining(", "));
            throw new InvalidRequestException(message);
        }

        return request;
    }

    private String formatViolation(ConstraintViolation<HorseRequest> violation) {
        String path = violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
        return path.isBlank() ? violation.getMessage() : path + " " + violation.getMessage();
    }
}
