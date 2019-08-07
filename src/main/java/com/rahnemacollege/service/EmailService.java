package com.rahnemacollege.service;

import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Async
    public void sendPassRecoveryMail(User user,String appUrl) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(user.getEmail());
        String text = "To reset your password, click the link below:\n" + appUrl
                + "/reset?token=";
//                + user.getResetToken();
        helper.setText(text);
        helper.setSubject("AucApp password recovery");
        mailSender.send(message);
    }

}
