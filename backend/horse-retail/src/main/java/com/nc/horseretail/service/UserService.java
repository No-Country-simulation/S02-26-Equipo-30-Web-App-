package com.nc.horseretail.service;

import com.nc.horseretail.dto.*;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.model.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getMe(User domainUser);

    UserResponse updateMe(User domainUser, UserUpdateRequest request);

    void changePassword(User domainUser, PasswordUpdateRequest request);

    void deleteMe(User domainUser);

    UserResponse getUserPublicProfile(UUID userId);

    Page<ListingResponse> getUserActiveListings(UUID userId, Pageable pageable);

    UserResponse updateUserRole(UUID userId, Role role);

    UserResponse updateUserStatus(UUID userId, UserStatus status);

    void deleteUser(UUID userId);

    Page<HorseResponse> getUserHorses(UUID userId, Pageable pageable);

    void banUser(UUID userId);

    void unbanUser(UUID userId);

    UserResponse getUserById(UUID userId);
}