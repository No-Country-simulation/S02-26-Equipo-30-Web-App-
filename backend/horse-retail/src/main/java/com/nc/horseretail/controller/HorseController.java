package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.service.HorseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Operation(summary = "Create a new horse", description = "Creates a new horse with the provided details")
    @ApiResponse(responseCode = "200", description = "Horse created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping
    public ResponseEntity<Void> createHorse(@Valid @RequestBody HorseRequest request,
                                            @AuthenticationPrincipal SecurityUser securityUser) {
        log.info("Received request to create horse");
        horseService.createHorse(request, securityUser.getDomainUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ============================
    // GET ALL HORSES
    // ============================
    @Operation(summary = "Get all horses", description = "Retrieves a list of all horses")
    @ApiResponse(responseCode = "200", description = "Horses retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<List<HorseResponse>> getAllHorses() {
        log.info("Received request to get all horses");
        List<HorseResponse> horses = horseService.getAllHorses();
        return ResponseEntity.ok(horses);
    }

    // ============================
    // GET HORSE BY ID
    // ============================
    @Operation(summary = "Get horse by ID", description = "Retrieves details of a specific horse by its ID")
    @ApiResponse(responseCode = "200", description = "Horse retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Horse not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/{id}")
    public ResponseEntity<HorseResponse> getHorseById(@PathVariable UUID id) {
        log.info("Received request to get horse by ID: {}", id);
        return ResponseEntity.ok(horseService.getHorseById(id));
    }

    // ============================
    // UPDATE HORSE
    // ============================
    @Operation(summary = "Update horse details", description = "Updates the details of an existing horse")
    @ApiResponse(responseCode = "200", description = "Horse updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Horse not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PatchMapping("/{id}")
    public ResponseEntity<HorseResponse> updateHorse(@PathVariable UUID id,
                                                     @Valid @RequestBody HorseRequest request,
                                                     @AuthenticationPrincipal SecurityUser securityUser) {
        log.info("Received request to update horse with ID: {}", id);
        return ResponseEntity.ok(horseService.updateHorse(id, request, securityUser.getDomainUser()));
    }

    // ============================
    // COUNT HORSES
    // ============================
    @Operation(summary = "Count all horses", description = "Returns the total number of horses available")
    @ApiResponse(responseCode = "200", description = "Horse count retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/count")
    public ResponseEntity<Long> countHorses() {
        log.info("Received request to count all horses");
        return ResponseEntity.ok(horseService.countTotalHorses());
    }

    // ============================
    // SEARCH HORSES
    // ============================
    @Operation(summary = "Search horses", description = "Filter by name, breed or discipline")
    @ApiResponse(responseCode = "200", description = "Horses retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/search")
    public ResponseEntity<List<HorseResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(horseService.searchHorses(keyword));
    }
}
