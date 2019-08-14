package com.rahnemacollege.controller;

import com.rahnemacollege.domain.*;
import com.rahnemacollege.model.ResetRequest;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.*;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.PathParam;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Transactional
public class UserController {

    private final UserService userService;
    private final ResourceAssembler assembler;
    private final PasswordService passwordService;
    private UserDetailsServiceImpl detailsService;
    private final ResetRequestService requestService;
    private final EmailService emailService;
    private final TokenUtil tokenUtil;
    private final Logger log;
    private final Validator validator;


    public UserController(UserService userService, ResourceAssembler assembler, PasswordService passwordService,
                          UserDetailsServiceImpl userDetailService, UserDetailsServiceImpl detailsService, ResetRequestService requestService, EmailService emailService, TokenUtil tokenUtil, Validator validator) {
        this.userService = userService;
        this.assembler = assembler;
        this.passwordService = passwordService;
        this.detailsService = userDetailService;
        this.detailsService = detailsService;
        this.requestService = requestService;
        this.emailService = emailService;
        this.tokenUtil = tokenUtil;
        log = LoggerFactory.getLogger(UserController.class);
        this.validator = validator;
    }


    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        log.info(authenticationRequest.getEmail() + " wants to login *_*");
        return userService.auth(authenticationRequest.getEmail(), authenticationRequest.getPassword());

    }


    @PostMapping("/edit")
    public AuthenticationResponse edit(@RequestBody SimpleUserDomain userDomain) {
        log.info(detailsService.getUser().getName() + " with id " + detailsService.getUser().getId() + " try to edit his name or email");
        String email = userDomain.getEmail();
        String name = userDomain.getName();
        if (email != null)
            email = email.toLowerCase();
        User user = userService.edit(name, email);
        log.info(user.getName() + " changed his/her infos :)");
        final UserDetails userDetails = detailsService.loadUserByUsername(user.getEmail());
        return new AuthenticationResponse(tokenUtil.generateToken(userDetails));
    }

    @PostMapping("/edit/picture")
    public ResponseEntity<SimpleUserDomain> setUserPicture(@RequestPart MultipartFile picture){
        log.info(detailsService.getUser().getName() + " with id " + detailsService.getUser().getId() + " try to set a profile picture");
        return new ResponseEntity<>(userService.setPicture(picture),HttpStatus.OK);
    }

    @RequestMapping(value = "/edit/password", method = RequestMethod.POST)
    public Resource<SimpleUserDomain> setNewPassword(@RequestParam("oPassword") String oPassword,
                                               @RequestParam("nPassword") String nPassword) {
        User user = detailsService.getUser();
        if (nPassword == null || oPassword == null || nPassword.length() < 6)
            throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
        if (!passwordService.getPasswordEncoder().matches(oPassword,user.getPassword()))
            throw new InvalidInputException(Message.PASSWORD_INCORRECT);
        return assembler.toResource(userService.changePassword(user, nPassword));

    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<SimpleUserDomain> add(@RequestBody AddUserDomain userDomain) {
        log.info("someone try to sign up ._.");
        if (userDomain.getName() == null || userDomain.getName().length() < 1)
            throw new InvalidInputException(Message.NAME_NULL);
        if (userDomain.getEmail() == null)
            throw new InvalidInputException(Message.EMAIL_NULL);
        if (userDomain.getPassword() == null)
            throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
        userDomain.setEmail(userDomain.getEmail().toLowerCase());
        SimpleUserDomain user = userService.addUser(userDomain.getName(), userDomain.getEmail(), userDomain.getPassword());
        log.info(user.getName() + " added to DB :)");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDomain> one() {
        log.info(detailsService.getUser().getName() + " with id " + detailsService.getUser().getId() + " try to get him/her infos");
        UserDomain user = userService.toUserDomain(detailsService.getUser());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    //TODO : refactoring
    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public Resource<UserDomain> processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request) {
        userEmail = userEmail.toLowerCase();
        System.out.println(userEmail);
        Optional<User> optional = userService.findUserByEmail(userEmail);
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
            System.err.println("successMessage" + " A validPassword reset link has been sent to " + userEmail + " @" +
                    new Date());
            return assembler.toResource(userService.toUserDomain(optional.get()));
        }
    }

    //TODO : refactoring
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public Resource<UserDomain> displayResetPasswordPage(@PathParam("token") String token) {
        Optional<ResetRequest> request = requestService.findByToken(token);
        if (request.isPresent()) {
//            todo: redirect to validPassword reset page
            System.err.println("redirecting to pass reset screen");
            return assembler.toResource(userService.toUserDomain(request.get().getUser()));
        } else {
            System.err.println("errorMessage : Oops!  This is an invalid validPassword reset link.");
            throw new InvalidInputException(Message.INVALID_RESET_LINK);
        }
    }

    //TODO : refactoring
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Resource<UserDomain> setNewPassword(@RequestParam Map<String, String> requestParams) {
        ResetRequest request = requestService.findByToken(requestParams.get("token")).orElseThrow(() -> new InvalidInputException(Message.TOKEN_NOT_FOUND));
        User resetUser = request.getUser();
        if (resetUser != null) {
            resetUser.setPassword(passwordService.getPasswordEncoder().encode(requestParams.get("validPassword")));
            requestService.removeRequest(request);
            userService.addUser(resetUser);
            System.err.println("successMessage: You have successfully reset your validPassword.  You may now login.");
            //TODO : redirect:login
            return assembler.toResource(userService.toUserDomain(resetUser));
        } else {
            System.err.println("errorMessage : Oops!  This is an invalid validPassword reset link.");
            throw new InvalidInputException(Message.NOT_RECORDED_REQUEST);
        }
    }

    // TODO: redirect to login page


}
