package com.nc.horseretail.service;

public interface EmailService {

    void sendPasswordResetEmail(String recipientEmail, String token);
}
