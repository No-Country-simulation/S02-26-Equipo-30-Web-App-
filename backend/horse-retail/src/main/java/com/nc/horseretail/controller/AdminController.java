package com.nc.horseretail.controller;

import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.dto.UserResponse;
import com.nc.horseretail.dto.admin.AuditLogResponse;
import com.nc.horseretail.dto.admin.SystemHealthResponse;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative operations")
public class AdminController {

    private final UserService userService;
    private final ListingService listingService;
    private final HorseService horseService;
    private final AdminSystemService adminSystemService;

    // ============================
    // USERS MANAGEMENT
    // ============================

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        log.info("Admin fetching all users");
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PatchMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID userId) {
        userService.banUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID userId) {
        userService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<Void> changeUserRole(
            @PathVariable UUID userId,
            @RequestParam Role role
    ) {
        userService.updateUserRole(userId, role);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // LISTINGS MANAGEMENT
    // ============================

    @GetMapping("/listings")
    public ResponseEntity<Page<ListingResponse>> getAllListings(Pageable pageable) {
        return ResponseEntity.ok(listingService.getAllListings(pageable));
    }

    @DeleteMapping("/listings/{listingId}")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID listingId) {
        listingService.deleteListingByAdmin(listingId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/listings/{listingId}/force-close")
    public ResponseEntity<Void> forceCloseListing(@PathVariable UUID listingId) {
        listingService.forceCloseListing(listingId);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // HORSES MANAGEMENT
    // ============================

    @DeleteMapping("/horses/{horseId}")
    public ResponseEntity<Void> deleteHorse(@PathVariable UUID horseId) {
        horseService.deleteHorseByAdmin(horseId);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // SYSTEM
    // ============================

    @GetMapping("/system/health")
    public ResponseEntity<SystemHealthResponse> getSystemHealth() {
        return ResponseEntity.ok(adminSystemService.getSystemHealth());
    }

    @GetMapping("/system/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(adminSystemService.getAuditLogs(pageable));
    }
}