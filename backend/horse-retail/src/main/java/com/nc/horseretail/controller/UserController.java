package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.dto.UserResponse;
import com.nc.horseretail.dto.UserUpdateRequest;
import com.nc.horseretail.dto.PasswordUpdateRequest;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.UserStatus;
import com.nc.horseretail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "User management API")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ============================
    // GET MY PROFILE
    // ============================
    @Operation(summary = "Get my profile", description = "Returns authenticated user's profile")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(
                userService.getMe(securityUser.getDomainUser())
        );
    }

    // ============================
    // UPDATE MY PROFILE
    // ============================
    @Operation(summary = "Update my profile", description = "Updates authenticated user's profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(
                userService.updateMe(securityUser.getDomainUser(), request)
        );
    }

    // ============================
    // CHANGE PASSWORD
    // ============================
    @Operation(summary = "Change my password", description = "Updates authenticated user's password")
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordUpdateRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {

        userService.changePassword(securityUser.getDomainUser(), request);
        return ResponseEntity.ok().build();
    }

    // ============================
    // DELETE MY ACCOUNT
    // ============================
    @Operation(summary = "Delete my account", description = "Soft deletes authenticated user")
    @ApiResponse(responseCode = "204", description = "Account deleted")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(
            @AuthenticationPrincipal SecurityUser securityUser) {

        userService.deleteMe(securityUser.getDomainUser());
        return ResponseEntity.noContent().build();
    }

    // ============================
    // GET USER PUBLIC PROFILE
    // ============================
    @Operation(summary = "Get user profile", description = "Returns public user profile")
    @ApiResponse(responseCode = "200", description = "User retrieved")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                userService.getUserPublicProfile(userId)
        );
    }

    // ============================
    // GET USER LISTINGS
    // ============================
    @Operation(summary = "Get user listings", description = "Returns active listings of a user")
    @ApiResponse(responseCode = "200", description = "Listings retrieved")
    @GetMapping("/{userId}/listings")
    public ResponseEntity<Page<ListingResponse>> getUserListings(
            @PathVariable UUID userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                userService.getUserActiveListings(userId, pageable)
        );
    }

    // ============================
    // GET USER HORSES
    // ============================
    @Operation(summary = "Get user horses", description = "Returns horses owned by user")
    @ApiResponse(responseCode = "200", description = "Horses retrieved")
    @GetMapping("/{userId}/horses")
    public ResponseEntity<Page<HorseResponse>> getUserHorses(
            @PathVariable UUID userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                userService.getUserHorses(userId, pageable)
        );
    }

    // ============================
    // ADMIN: GET ALL USERS
    // ============================
    @Operation(summary = "Get all users", description = "Admin only - returns paginated users")
    @ApiResponse(responseCode = "200", description = "Users retrieved")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {

        return ResponseEntity.ok(
                userService.getAllUsers(pageable)
        );
    }

    // ============================
    // ADMIN: UPDATE USER ROLE
    // ============================
    @Operation(summary = "Update user role", description = "Admin only - change user role")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam Role role) {

        return ResponseEntity.ok(
                userService.updateUserRole(userId, role)
        );
    }

    // ============================
    // ADMIN: UPDATE USER STATUS
    // ============================
    @Operation(summary = "Update user status", description = "Admin only - activate/suspend user")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID userId,
            @RequestParam UserStatus status) {

        return ResponseEntity.ok(
                userService.updateUserStatus(userId, status)
        );
    }

    // ============================
    // ADMIN: DELETE USER
    // ============================
    @Operation(summary = "Delete user", description = "Admin only - deletes a user")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}