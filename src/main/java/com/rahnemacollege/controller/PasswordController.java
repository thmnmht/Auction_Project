package com.rahnemacollege.controller;

import com.rahnemacollege.model.ResetRequest;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.*;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@Transactional
public class PasswordController {

    private final UserService service;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final ResourceAssembler assembler;
    private final ResetRequestService requestService;

    @Autowired
    public PasswordController(UserService service, PasswordService passwordService,
                              EmailService emailService, ResourceAssembler assembler,
                              ResetRequestService requestService) {
        this.service = service;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.assembler = assembler;
        this.requestService = requestService;
    }


    // Process form submission from forgotPassword page
    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public Resource<User> processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request) {
        Optional<User> optional = service.findUserByEmail(userEmail);
        if (!optional.isPresent()) {
            System.err.println("email not found on DB");
            throw new InvalidInputException(Message.EMAIL_NOT_FOUND);
        } else {
            String token;
            ResetRequest resetRequest;
            if (requestService.findByUser(optional.get()).isPresent()) {
                if (new Date().getTime() - requestService.findByUser(optional.get()).get().getDate().getTime() < 10800000) {
                    token = requestService.findByUser(optional.get()).get().getToken();
                } else {
                    resetRequest = requestService.findByUser(optional.get()).get();
                    token = UUID.randomUUID().toString();
                    resetRequest.setToken(token);
                    requestService.addRequest(resetRequest);
                }
            } else {
                token = UUID.randomUUID().toString();
                resetRequest = new ResetRequest(optional.get(), new Date(), token);
                requestService.addRequest(resetRequest);
            }

            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            try {
                emailService.sendPassRecoveryMail(userEmail, appUrl, token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Add success message to view
            System.err.println("successMessage" + " A password reset link has been sent to " + userEmail + " @" +
                    new Date());
            return assembler.toResource(optional.get());
        }
    }

    //     Display form to reset password
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public Resource<User> displayResetPasswordPage(@RequestParam("token") String token) {
        Optional<ResetRequest> request = requestService.findByToken(token);
        if (request.isPresent()) { // Token found in DB
//            todo: redirect to password reset page
            System.err.println("redirecting to pass reset screen");
            return assembler.toResource(request.get().getUser());
        } else { // Token not found in DB
            System.err.println("errorMessage : Oops!  This is an invalid password reset link.");
            throw new InvalidInputException(Message.INVALID_RESET_LINK);
        }
    }

    //     Process reset password form
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Resource<User> setNewPassword(@RequestParam Map<String, String> requestParams) {

        // Find the user associated with the reset token
        ResetRequest request = requestService.findByToken(requestParams.get("token")).orElseThrow(() -> new  InvalidInputException(Message.TOKEN_NOT_FOUND));
        User resetUser = request.getUser();

        // This should always be non-null but we check just in case
        if (resetUser != null) {

            // Set new password
            resetUser.setPassword(passwordService.getPasswordEncoder().encode(requestParams.get("password")));
            // Set the reset token to null so it cannot be used again
            requestService.removeRequest(request);

            service.addUser(resetUser);

            // In order to set a model attribute on a redirect, we must use
            // RedirectAttributes
            System.err.println("successMessage: You have successfully reset your password.  You may now login.");

            //TODO : redirect:login

            return assembler.toResource(resetUser);

        } else {
            System.err.println("errorMessage : Oops!  This is an invalid password reset link.");
            throw new  InvalidInputException(Message.NOT_RECORDED_REQUEST);
        }
    }

    // Going to reset page without a token redirects to login page
    /* TODO: redirect to login page
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:login");
    }
    */
}
