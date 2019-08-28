package com.rahnemacollege.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@EnableAsync
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendPassRecoveryMail(String userEmail, String appUrl, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("fivegears.rahnema@gmail.com");
        helper.setTo(userEmail);
        String text = "To reset your validPassword, click the link below:\n" + appUrl
                + "/users/reset?token=" + token;
        helper.setText(text);
        helper.setSubject("AucApp validPassword recovery");
        mailSender.send(message);
    }
}
