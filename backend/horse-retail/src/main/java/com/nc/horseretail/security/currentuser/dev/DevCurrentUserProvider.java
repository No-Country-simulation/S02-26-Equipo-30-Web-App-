package com.nc.horseretail.security.currentuser.dev;

import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.UserRepository;
import com.nc.horseretail.security.currentuser.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevCurrentUserProvider implements CurrentUserProvider {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        return userRepository.findByEmail("dev@horseretail.com")
                .orElseThrow(() -> new IllegalStateException("Dev user not found"));
    }
}