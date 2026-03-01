package com.nc.horseretail.service;

import com.nc.horseretail.config.JwtService;
import com.nc.horseretail.dto.auth.AuthRequest;
import com.nc.horseretail.dto.auth.AuthResponse;
import com.nc.horseretail.dto.auth.RegisterRequest;
import com.nc.horseretail.dto.auth.ResetPasswordRequest;
import com.nc.horseretail.dto.auth.VerifyEmailRequest;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.EmailAlreadyExistException;
import com.nc.horseretail.exception.InvalidCredentialException;
import com.nc.horseretail.model.auth.EmailVerificationToken;
import com.nc.horseretail.model.auth.PasswordResetToken;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.model.user.UserStatus;
import com.nc.horseretail.repository.EmailVerificationTokenRepository;
import com.nc.horseretail.repository.PasswordResetTokenRepository;
import com.nc.horseretail.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final int VERIFICATION_CODE_DIGITS = 6;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${app.email-verification.code-ttl-minutes:10}")
    private long verificationCodeTtlMinutes;

    // ============================
    // REGISTER
    // ============================

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getEmail())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        user.setAccountEnabled(false);
        user.setEmailVerified(false);
        userRepository.save(user);

        emailVerificationTokenRepository.deleteByUser(user);
        String verificationCode = generateVerificationCode();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .code(verificationCode)
                .user(user)
                .expiryDate(Instant.now().plus(verificationCodeTtlMinutes, ChronoUnit.MINUTES))
                .build();
        emailVerificationTokenRepository.save(verificationToken);
        emailService.sendEmailVerificationCode(user.getEmail(), verificationCode);

        return AuthResponse.builder()
                .emailVerificationRequired(true)
                .message("Verification code sent to your email")
                .build();
    }

    // ============================
    // AUTHENTICATE
    // ============================

    @Override
    public AuthResponse authenticate(AuthRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialException("Account is not active");
        }
        if (!user.isEmailVerified() || !user.isAccountEnabled()) {
            throw new InvalidCredentialException("Email is not verified");
        }

        var claims = buildClaims(user);

        String accessToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // ============================
    // REFRESH TOKEN
    // ============================

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String email;

        try {
            email = jwtService.getUserName(refreshToken);
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        if (email == null || !jwtService.isTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account inactive");
        }
        if (!user.isEmailVerified() || !user.isAccountEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email is not verified");
        }

        var claims = buildClaims(user);

        String newAccessToken = jwtService.generateToken(claims, user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    // ============================
    // VERIFY EMAIL
    // ============================

    @Override
    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid or expired verification code"));

        EmailVerificationToken verificationToken = emailVerificationTokenRepository
                .findTopByUserAndCodeOrderByCreatedAtDesc(user, request.getCode())
                .orElseThrow(() -> new BusinessException("Invalid or expired verification code"));

        if (!verificationToken.isValid()) {
            throw new BusinessException("Invalid or expired verification code");
        }

        verificationToken.setUsed(true);
        emailVerificationTokenRepository.save(verificationToken);
        emailVerificationTokenRepository.deleteByUser(user);

        user.setEmailVerified(true);
        user.setAccountEnabled(true);
        userRepository.save(user);

        var claims = buildClaims(user);
        String accessToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .message("Email verified successfully")
                .build();
    }

    // ============================
    // FORGOT PASSWORD
    // ============================

    @Override
    @Transactional
    public void forgotPassword(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        String token = UUID.randomUUID().toString();

        passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(30, ChronoUnit.MINUTES))
                .build();

        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        log.info("Password reset token generated for user {}", user.getEmail());
    }

    // ============================
    // RESET PASSWORD
    // ============================

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired token"));

        if (!resetToken.isValid()) {
            throw new BusinessException("Invalid or expired token");
        }

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        User user = resetToken.getUser();

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        log.info("Password successfully reset for user {}", user.getEmail());
    }

    // ============================
    // LOGOUT
    // ============================

    @Override
    public void logout(UUID userId) {

        User domainUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Invalidar refresh tokens persistidos

        log.info("User {} logged out", domainUser.getUsername());
    }

    // ============================
    // PRIVATE
    // ============================

    private Map<String, Object> buildClaims(User user) {
        return Map.of(
                ROLES, List.of(ROLE_PREFIX + user.getRole().name())
        );
    }

    private String generateVerificationCode() {
        int bound = (int) Math.pow(10, VERIFICATION_CODE_DIGITS);
        int code = ThreadLocalRandom.current().nextInt(bound);
        return String.format("%0" + VERIFICATION_CODE_DIGITS + "d", code);
    }
}
