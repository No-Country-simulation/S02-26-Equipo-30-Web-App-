package com.nc.horseretail.service;

public interface EmailService {

    void sendPasswordResetEmail(String recipientEmail, String token);

    void sendEmailVerificationCode(String recipientEmail, String code);
}
