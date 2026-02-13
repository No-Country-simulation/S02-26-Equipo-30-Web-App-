package com.nc.horseretail.security.currentuser;

import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProviderImpl implements CurrentUserProvider {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        // Buscamos el primer usuario de la tabla tbl_users para simular que está logueado
        return userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay usuarios en la base de datos para simular el login. Crea uno primero."));
    }
}