package com.nc.horseretail.controller;

import com.nc.horseretail.dto.UserResponse;
import com.nc.horseretail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    // ============================
    // ADMIN ACCESS
    // ============================
    @GetMapping("/access")
    public ResponseEntity<String> access() {
        return new ResponseEntity<>("Access Granted", HttpStatus.OK);
    }

    // ============================
    // GET ALL USERS WITH PAGINATION
    // ============================
    @Operation(summary = "Get all users with pagination")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

//TODO GET /users/{userId}  → ADMIN ONLY
//TODO PATCH /users/{userId}/ban  → ADMIN ONLY
//TODO PATCH /users/{userId}/unban  → ADMIN ONLY
//TODO PATCH /users/{userId}/role  → ADMIN ONLY
//TODO DELETE /users/{userId}  → ADMIN ONLY

//TODO GET /listings  → ADMIN ONLY
//TODO DELETE /listings/{listingId}  → ADMIN ONLY
//TODO PATCH /listings/{listingId}/force-close  → ADMIN ONLY

//TODO DELETE /horses/{horseId}  → ADMIN ONLY

//TODO GET /system/health  → ADMIN ONLY
//TODO GET /system/audit-logs  → ADMIN ONLY
}
