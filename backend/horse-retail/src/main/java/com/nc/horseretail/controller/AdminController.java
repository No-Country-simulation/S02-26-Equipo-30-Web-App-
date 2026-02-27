package com.nc.horseretail.controller;

import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.dto.UserResponse;
import com.nc.horseretail.dto.admin.AuditLogResponse;
import com.nc.horseretail.dto.admin.SystemHealthResponse;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(
        name = "Admin",
        description = "Administrative operations for managing users, listings, horses and system monitoring"
)
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;
    private final ListingService listingService;
    private final HorseService horseService;
    private final AdminSystemService adminSystemService;

    // ============================
    // USERS MANAGEMENT
    // ============================

    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of all registered users. ADMIN role required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Pagination information")
            Pageable pageable
    ) {
        log.info("Admin fetching all users");
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves detailed information about a specific user by their ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(
            summary = "Ban user",
            description = "Bans a user, preventing them from accessing the platform."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User banned successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable UUID userId
    ) {
        userService.updateUserStatus(userId, com.nc.horseretail.model.user.UserStatus.SUSPENDED);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Unban user",
            description = "Restores access to a previously banned user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User unbanned successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable UUID userId
    ) {
        userService.updateUserStatus(userId, com.nc.horseretail.model.user.UserStatus.ACTIVE);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Change user role",
            description = "Updates the role of a specific user (e.g., USER or ADMIN)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role supplied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<Void> changeUserRole(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "New role to assign", required = true)
            @RequestParam Role role
    ) {
        userService.updateUserRole(userId, role);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete user",
            description = "Permanently deletes a user and all associated data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable UUID userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // LISTINGS MANAGEMENT
    // ============================

    @Operation(
            summary = "Get all listings",
            description = "Returns a paginated list of all listings in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listings retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/listings")
    public ResponseEntity<Page<ListingResponse>> getAllListings(
            @Parameter(description = "Pagination information")
            Pageable pageable
    ) {
        return ResponseEntity.ok(listingService.getAllListings(pageable));
    }

    @Operation(
            summary = "Delete listing",
            description = "Deletes a listing from the system as an administrative action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Listing deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/listings/{listingId}")
    public ResponseEntity<Void> deleteListing(
            @Parameter(description = "Listing unique identifier", required = true)
            @PathVariable UUID listingId
    ) {
        listingService.deleteListingByAdmin(listingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Force close listing",
            description = "Closes a listing administratively without marking it as sold."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Listing closed successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/listings/{listingId}/force-close")
    public ResponseEntity<Void> forceCloseListing(
            @Parameter(description = "Listing unique identifier", required = true)
            @PathVariable UUID listingId
    ) {
        listingService.forceCloseListing(listingId);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // HORSES MANAGEMENT
    // ============================

    @Operation(
            summary = "Delete horse",
            description = "Deletes a horse record from the system as an administrative action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horse deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Horse not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/horses/{horseId}")
    public ResponseEntity<Void> deleteHorse(
            @Parameter(description = "Horse unique identifier", required = true)
            @PathVariable UUID horseId
    ) {
        horseService.deleteHorseByAdmin(horseId);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // SYSTEM
    // ============================

    @Operation(
            summary = "Get system health",
            description = "Returns overall system health metrics and platform statistics."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "System health retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/system/health")
    public ResponseEntity<SystemHealthResponse> getSystemHealth() {
        return ResponseEntity.ok(adminSystemService.getSystemHealth());
    }

    @Operation(
            summary = "Get audit logs",
            description = "Returns paginated system audit logs for administrative review."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/system/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @Parameter(description = "Pagination information")
            Pageable pageable
    ) {
        return ResponseEntity.ok(adminSystemService.getAuditLogs(pageable));
    }
}