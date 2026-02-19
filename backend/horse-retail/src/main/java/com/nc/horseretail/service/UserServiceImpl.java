package com.nc.horseretail.service;

import com.nc.horseretail.dto.*;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.UserMapper;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.model.user.UserStatus;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ListingRepository listingRepository;
    private final HorseRepository horseRepository;
    private final PasswordEncoder passwordEncoder;

    // ============================
    // ADMIN - GET ALL USERS
    // ============================

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    // ============================
    // GET MY PROFILE
    // ============================

    @Override
    public UserResponse getMe(User domainUser) {
        return userMapper.toDto(domainUser);
    }

    // ============================
    // UPDATE MY PROFILE
    // ============================

    @Override
    public UserResponse updateMe(User domainUser, UserUpdateRequest request) {

        if (!domainUser.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email already in use");
            }
            domainUser.setEmail(request.getEmail());
            domainUser.setEmailVerified(false);
        }

        domainUser.setFullName(request.getFullName());

        userRepository.save(domainUser);

        return userMapper.toDto(domainUser);
    }

    // ============================
    // CHANGE PASSWORD
    // ============================

    @Override
    public void changePassword(User domainUser, PasswordUpdateRequest request) {

        if (!passwordEncoder.matches(request.getCurrentPassword(), domainUser.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("New passwords do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), domainUser.getPasswordHash())) {
            throw new BusinessException("New password must be different from current password");
        }

        domainUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(domainUser);

        log.info("User {} changed password successfully", domainUser.getUsername());
    }

    // ============================
    // DELETE MY ACCOUNT (SOFT DELETE)
    // ============================

    @Override
    public void deleteMe(User domainUser) {

        domainUser.setStatus(UserStatus.DEACTIVATED);

        listingRepository.findByOwnerAndStatus(domainUser, ListingStatus.ACTIVE, Pageable.unpaged())
                .forEach(listing -> listing.setStatus(ListingStatus.PAUSED));

        userRepository.save(domainUser);

        log.info("User {} deactivated their account", domainUser.getUsername());
    }

    // ============================
    // GET PUBLIC USER PROFILE
    // ============================

    @Override
    public UserResponse getUserPublicProfile(UUID userId) {

        User user = findUserOrThrow(userId);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return userMapper.toPublicDto(user);
    }

    // ============================
    // GET USER ACTIVE LISTINGS (PUBLIC)
    // ============================

    @Override
    public Page<ListingResponse> getUserActiveListings(UUID userId, Pageable pageable) {

        User user = findActiveUser(userId);

        return listingRepository
                .findByOwnerAndStatus(user, ListingStatus.ACTIVE, pageable)
                .map(userMapper::toListingDto);
    }

    // ============================
    // GET USER HORSES
    // ============================

    @Override
    public Page<HorseResponse> getUserHorses(UUID userId, Pageable pageable) {

        User user = findActiveUser(userId);

        return horseRepository.findByOwner(user, pageable)
                .map(userMapper::toHorseDto);
    }


    @Override
    public void banUser(UUID userId) {
//TODO implement method
        throw new BusinessException("Method not implemented yet");
    }

    @Override
    public void unbanUser(UUID userId) {
//TODO implement method
        throw new BusinessException("Method not implemented yet");
    }

    @Override
    public UserResponse getUserById(UUID userId) {
            User user = findUserOrThrow(userId);
        return userMapper.toDto(user);
    }

    // ============================
    // ADMIN - UPDATE ROLE
    // ============================

    @Override
    public UserResponse updateUserRole(UUID userId, Role role) {

        User user = findUserOrThrow(userId);

        if (user.getRole() == Role.ADMIN && role != Role.ADMIN) {
            throw new BusinessException("Cannot downgrade another admin without special process");
        }

        user.setRole(role);

        userRepository.save(user);

        log.info("Updated role for user {} to {}", user.getUsername(), role);

        return userMapper.toDto(user);
    }

    // ============================
    // ADMIN - UPDATE STATUS
    // ============================

    @Override
    public UserResponse updateUserStatus(UUID userId, UserStatus status) {

        User user = findUserOrThrow(userId);

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException("Cannot modify a deleted user");
        }

        user.setStatus(status);

        if (status == UserStatus.SUSPENDED) {
            listingRepository.findByOwnerAndStatus(user, ListingStatus.ACTIVE, Pageable.unpaged())
                    .forEach(listing -> listing.setStatus(ListingStatus.PAUSED));
        }

        userRepository.save(user);

        log.info("Updated status for user {} to {}", user.getUsername(), status);

        return userMapper.toDto(user);
    }

    // ============================
    // ADMIN - DELETE USER (SOFT DELETE)
    // ============================

    @Override
    public void deleteUser(UUID userId) {

        User user = findUserOrThrow(userId);

        user.setStatus(UserStatus.DELETED);

        listingRepository.findByOwnerAndStatus(user, ListingStatus.ACTIVE, Pageable.unpaged())
                .forEach(listing -> listing.setStatus(ListingStatus.CANCELLED));

        userRepository.save(user);

        log.warn("Admin deleted user {}", user.getUsername());
    }

    // ============================
    // PRIVATE HELPERS
    // ============================


    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    private User findVisibleUser(UUID id) {

        User user = findUserOrThrow(id);

        if (user.getStatus() == UserStatus.DELETED) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        return user;
    }

    private User findActiveUser(UUID id) {

        User user = findVisibleUser(id);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        return user;
    }
}