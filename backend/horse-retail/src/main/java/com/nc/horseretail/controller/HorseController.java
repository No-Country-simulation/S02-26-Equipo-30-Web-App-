package com.nc.horseretail.controller;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.service.HorseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Horse", description = "API for managing horses")
@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
@Slf4j
public class HorseController {

    private final HorseService horseService;

    @Operation(summary = "Create a new horse", description = "Creates a new horse with the provided details")
    @ApiResponse(responseCode = "200", description = "Horse created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping
    public ResponseEntity<Void> createHorse(@Valid @RequestBody HorseRequest request) {
        log.info("Received request to create horse");
        horseService.createHorse(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all horses", description = "Retrieves a list of all horses")
    @ApiResponse(responseCode = "200", description = "Horses retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<List<HorseResponse>> getAllHorses() {
        log.info("Received request to get all horses");
        List<HorseResponse> horses = horseService.getAllHorses();
        return ResponseEntity.ok(horses);
    }

    //TODO get by id, count, update endpoints



}
