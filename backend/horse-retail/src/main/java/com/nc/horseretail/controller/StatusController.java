package com.nc.horseretail.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Status", description = "API for checking the status of the application")
@Validated
@RestController
@RequestMapping("/api/v1/status")
@Slf4j
public class StatusController {

    @Operation(summary = "Get the status of the application")
    @ApiResponse(responseCode = "200", description = "Application is running")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok().body("Controller OK!");
    }
}
