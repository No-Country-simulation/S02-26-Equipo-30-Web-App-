package com.nc.horseretail.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/access")
    public ResponseEntity<String> access() {
        return new ResponseEntity<>("Access Granted", HttpStatus.OK);
    }
}
