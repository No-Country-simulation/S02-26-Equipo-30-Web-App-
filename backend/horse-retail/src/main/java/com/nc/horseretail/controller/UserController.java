package com.nc.horseretail.controller;

import com.nc.horseretail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "API for managing users and sellers")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Count total sellers", description = "Returns the total number of registered users")
    @GetMapping("/count")
    public ResponseEntity<Long> getSellerCount() {
        return ResponseEntity.ok(userService.countTotalUsers());
    }
}