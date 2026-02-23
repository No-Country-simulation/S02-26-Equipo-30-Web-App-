package com.nc.horseretail.config;

import com.nc.horseretail.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    private static final int CLOCK_SKEW_SECONDS = 30;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @PostConstruct
    public void logValues() {
        log.info("JWT Access Expiration: {}ms", jwtExpiration);
        log.info("JWT Refresh Expiration: {}ms", refreshExpiration);
    }

    public String generateToken(Map<String, Object> claims, User user) {
        SecurityUser userDetails = new SecurityUser(user);
        return buildToken(claims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        SecurityUser userDetails = new SecurityUser(user);
        return buildToken(Map.of(), userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> claims, SecurityUser userDetails, long expirationMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserName(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(getAllClaims(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}