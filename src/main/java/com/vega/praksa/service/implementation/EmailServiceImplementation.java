package com.vega.praksa.service.implementation;

import com.vega.praksa.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImplementation(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void sendVerificationEmail(String to, String token) throws MessagingException {
        String subject = "Email verification";
        String verificationUrl = "http://localhost:8080/auth/verify?token=" + token;
        String content = "<p>Please verify your email by clicking the link below:</p>"
                        + "<p><a href=\"" + verificationUrl + "\">Verify Email</a></p>";

        sendMail(to, subject, content);
    }

    @Override
    public void sendResetPasswordEmail(String to, String resetUrl) throws MessagingException {
        String subject = "Password Reset Request";
        String content = "<p>Click the link below to reset your password:</p>"
                + "<p><a href=\"" + resetUrl + "\">Reset Password</a></p>";

        sendMail(to, subject, content);
    }

    private void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

}
