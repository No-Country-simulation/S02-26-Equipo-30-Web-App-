package com.nc.horseretail.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    //TODO GET /me  → AUTH
//TODO PUT /me  → AUTH
//TODO PATCH /me/password  → AUTH
//TODO DELETE /me  → AUTH

//TODO GET /{userId}  → PUBLIC
//TODO GET /{userId}/listings  → PUBLIC
//TODO GET /{userId}/horses  → PUBLIC

//TODO GET /  → ADMIN ONLY
//TODO PATCH /{userId}/role  → ADMIN ONLY
//TODO PATCH /{userId}/status  → ADMIN ONLY
//TODO DELETE /{userId}  → ADMIN ONLY
}
