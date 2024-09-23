package com.vega.praksa.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendVerificationEmail(String to, String token) throws MessagingException;

}
