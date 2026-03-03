package com.nc.horseretail.controller;

import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/temp")
@RequiredArgsConstructor
public class TempController {

    private final UserRepository userRepository;

    @PatchMapping("/update")
    public String updateVerification(@RequestParam String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);

        return "User Updated: " + user.isEmailVerified();
    }
}
