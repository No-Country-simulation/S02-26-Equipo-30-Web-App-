package com.nc.horseretail.service;

import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //TODO implement method
//    @Override
//    public User getProfile(Authentication auth) {
//        return (User) auth.getPrincipal();
//    }

    @Override
    public User getCurrentUser() {

        //TODO implement method
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return null;
    }

}
