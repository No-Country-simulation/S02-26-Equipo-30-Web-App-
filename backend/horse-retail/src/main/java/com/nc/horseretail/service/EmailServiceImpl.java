package com.nc.horseretail.service;

import com.nc.horseretail.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@horse-trust.local}")
    private String fromAddress;

    @Value("${app.reset-password.base-url:http://localhost:5173/reset-password}")
    private String resetPasswordBaseUrl;

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String token) {
        String resetLink = buildResetLink(token);

        if (!mailEnabled) {
            log.info("Mail disabled. Password reset link for {}: {}", recipientEmail, resetLink);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipientEmail);
            message.setSubject("Horse Trust - Password Reset");
            message.setText("""
                    We received a request to reset your password.

                    Use the link below to set a new password:
                    %s

                    This link expires in 30 minutes.
                    If you did not request this, you can ignore this email.
                    """.formatted(resetLink));

            mailSender.send(message);
        } catch (Exception exception) {
            throw new ExternalServiceException("Failed to send password reset email");
        }
    }

    private String buildResetLink(String token) {
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return resetPasswordBaseUrl + "?token=" + encodedToken;
    }
}
