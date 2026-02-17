package com.nc.horseretail.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
<<<<<<< HEAD
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        "/api/v1/auth/**", 
                        "/v3/api-docs/**",
                        "/swagger-ui/**", 
                        "/swagger-ui.html",
                        "/api/v1/public", 
                        "/api/v1/metrics/sellers/count", 
                        "/api/v1/metrics/satisfaction"
                    ).permitAll()
                    .requestMatchers("/internal/ml/**").hasRole("ML_SERVICE")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/metrics/feedback").authenticated()
                    .anyRequest().authenticated()
=======
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/api/v1/auth/**", "/v3/api-docs/**",
                        "/swagger-ui/**", "/swagger-ui.html",
                        "/api/v1/public", "/api/v1/metrics/sellers/count", 
                        "/api/v1/metrics/satisfaction")

                        .permitAll()
                        .requestMatchers("/internal/ml/**").hasRole("ML_SERVICE")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/metrics/feedback").authenticated()
                        .anyRequest().authenticated()
>>>>>>> a8e503c5713434b62550b2c252fbbc742257c0c4
        )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://frontend.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}