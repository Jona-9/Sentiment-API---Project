package com.project.sentimentapi.service;

public interface EmailService {
    void sendRecoveryEmail(String to, String token);
}
