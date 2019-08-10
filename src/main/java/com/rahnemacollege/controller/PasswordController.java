package com.rahnemacollege.controller;

import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.EmailService;
import com.rahnemacollege.service.PasswordService;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.ResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class PasswordController {

    private final JavaMailSender sender;
    private final UserService service;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final ResourceAssembler assembler;

    @Autowired
    public PasswordController(JavaMailSender sender, UserService service, PasswordService passwordService, EmailService emailService, ResourceAssembler assembler) {
        this.sender = sender;
        this.service = service;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.assembler = assembler;
    }

    @RequestMapping("/sendMailTest")
    public String sendMail() {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo("sobhanebrahimi36@yahoo.com");
            helper.setText("Greetings :)");
            helper.setSubject("Mail From Spring Boot");
//            helper.addAttachment("document.PNG", file); //for attaching a file
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error while sending mail ..";
        }
        sender.send(message);
        return "Mail Sent Success!";
    }

    // Process form submission from forgotPassword page
    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public Resource<User> processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request) {
        User user ;
        Optional<User> optional = service.findUserByEmail(userEmail);
        if (!optional.isPresent()) {
            System.err.println("errorMessage"+ " Could not find an account for that e-mail address.");
        } else {
            // Generate random 36-character string token for reset password
             user = optional.get();
            user.setResetToken(UUID.randomUUID().toString());
            service.addUser(user);
            String appUrl = request.getScheme() + "://" + request.getServerName()+":"+request.getServerPort();
            try {
                emailService.sendPassRecoveryMail(user, appUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Add success message to view
            System.err.println("successMessage" +  " A password reset link has been sent to " + userEmail);
            return assembler.toResource(user);
        }
        return null;
    }

    // Display form to reset password
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public Resource<User> displayResetPasswordPage(@RequestParam("token") String token) {

        Optional<User> user = service.findUserByResetToken(token);

        if (user.isPresent()) { // Token found in DB
//            todo: redirect to password reset page
            System.err.println("redirecting to pass reset screen");
            return assembler.toResource(user.get());
        } else { // Token not found in DB
            System.err.println("errorMessage : Oops!  This is an invalid password reset link.");
            return null;
        }
    }

    // Process reset password form
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Resource<User> setNewPassword(@RequestParam Map<String, String> requestParams) {

        // Find the user associated with the reset token
        Optional<User> user = service.findUserByResetToken(requestParams.get("token"));

        // This should always be non-null but we check just in case
        if (user.isPresent()) {

            User resetUser = user.get();

            // Set new password
            resetUser.setPassword(passwordService.getPasswordEncoder().encode(requestParams.get("password")));
            // Set the reset token to null so it cannot be used again
            resetUser.setResetToken(null);


            service.addUser(resetUser);

            // In order to set a model attribute on a redirect, we must use
            // RedirectAttributes
            System.err.println("successMessage: You have successfully reset your password.  You may now login.");

            //TODO : redirect:login

            return assembler.toResource(user.get());

        } else {
            System.err.println("errorMessage : Oops!  This is an invalid password reset link.");
        }

        return null;
    }

    // Going to reset page without a token redirects to login page
    /* TODO: redirect to login page
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:login");
    }
    */
}
