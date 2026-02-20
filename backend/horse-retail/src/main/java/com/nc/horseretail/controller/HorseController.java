package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.service.HorseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Horse", description = "API for managing horses")
@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
@Slf4j
public class HorseController {

    private final HorseService horseService;

    // ============================
    // CREATE HORSE
    // ============================
    @Operation(summary = "Create a new horse", description = "Creates a new horse owned by the authenticated user")
    @ApiResponse(responseCode = "201", description = "Horse created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping
    public ResponseEntity<Void> createHorse(@Valid @RequestBody HorseRequest request,
                                            @AuthenticationPrincipal SecurityUser securityUser) {
        log.info("Creating horse for user {}", securityUser.getUsername());
        horseService.createHorse(request, securityUser.getDomainUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
                                                     @Valid @RequestBody HorseRequest request,
                                                     @AuthenticationPrincipal SecurityUser securityUser) {
        log.info("Updating horse {} by user {}", id, securityUser.getUsername());
        return ResponseEntity.ok(
                horseService.updateHorse(id, request, securityUser.getDomainUser())
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
        horseService.deleteHorse(id, securityUser.getDomainUser());
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
                horseService.getMyHorses(securityUser.getDomainUser())
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
                horseService.countMyHorses(securityUser.getDomainUser())
        );
    }
}
