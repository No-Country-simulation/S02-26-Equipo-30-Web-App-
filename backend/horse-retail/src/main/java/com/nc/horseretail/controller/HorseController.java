package com.nc.horseretail.controller;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.service.HorseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
@Slf4j
public class HorseController {

    private final HorseService horseService;

    @PostMapping
    public ResponseEntity<Void> createHorse(@Valid @RequestBody HorseRequest request) {
        log.info("Received request to create horse");
        horseService.createHorse(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<HorseResponse>> getAllHorses() {
        log.info("Received request to get all horses");
        List<HorseResponse> horses = horseService.getAllHorses();
        return ResponseEntity.ok(horses);
    }



}
