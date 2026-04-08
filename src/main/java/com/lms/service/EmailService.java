package com.lms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public boolean sendEmail(String to, String subject, String message) {
        if (to == null || to.isBlank()) {
            log.warn("Email was not sent because recipient is missing");
            return false;
        }
        if (mailUsername == null || mailUsername.isBlank() || "your-email@gmail.com".equals(mailUsername)) {
            log.warn("Email is not configured. Skipping email to {}", to);
            return false;
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            javaMailSender.send(mailMessage);
            log.info("Email sent to {} subject={}", to, subject);
            return true;
        } catch (RuntimeException ex) {
            log.warn("Email could not be sent to {}: {}", to, ex.getMessage());
            return false;
        }
    }
}
