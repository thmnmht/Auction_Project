package com.rahnemacollege.controller;

import com.google.gson.Gson;
import com.rahnemacollege.domain.AuthenticationRequest;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.ResetRequest;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.*;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.websocket.server.PathParam;
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



    public UserController(UserService userService, ResourceAssembler assembler, PasswordService passwordService,
                          UserDetailsServiceImpl userDetailService, UserDetailsServiceImpl detailsService, ResetRequestService requestService, EmailService emailService, TokenUtil tokenUtil) {
        this.userService = userService;
        this.assembler = assembler;
        this.passwordService = passwordService;
        this.detailsService = userDetailService;
        this.detailsService = detailsService;
        this.requestService = requestService;
        this.emailService = emailService;
        this.tokenUtil = tokenUtil;
    }


    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        if (!userService.isExist(authenticationRequest.getEmail()))
            throw new InvalidInputException(Message.EMAIL_NOT_FOUND);
        return userService.auth(authenticationRequest.getEmail(),authenticationRequest.getPassword());

    }

    @PostMapping("/edit")
    public AuthenticationResponse edit(String name, String email,@PathParam("picture") MultipartFile picture) {
        userService.setPicture(picture);
        User user = userService.edit(name, email);
        final UserDetails userDetails = detailsService.loadUserByUsername(user.getEmail());
        return new AuthenticationResponse(tokenUtil.generateToken(userDetails));
    }

    @RequestMapping(value = "/editpassword", method = RequestMethod.POST)
    public Resource<UserDomain> setNewPassword(@RequestParam("validPassword") String password,HttpServletRequest request) {
        String appUrl = request.getScheme() + "://" + request.getServerName();
        User user = detailsService.getUser();
        if (password != null && password.length() > 5 && password.length() < 100) {
            user.setPassword(passwordService.getPasswordEncoder().encode(password));
            UserDomain userDomain = userService.addUser(user.getName(), user.getEmail(), user.getPassword(),appUrl);

            return assembler.toResource(userDomain);
        }
        throw new InvalidInputException(Message.PASSWORD_INCORRECT);
    }



    @PostMapping("/signup")
    public Resource<UserDomain> add(@PathParam("name") String name, @PathParam("emil") String email, @PathParam("validPassword") String password,HttpServletRequest request) {
        System.out.println(name);
        String appUrl = request.getScheme() + "://" + request.getServerName();
        UserDomain user = userService.addUser(name, email, password,appUrl);
        return assembler.toResource(user);
    }


    //TODO : remove it! it's just for test
    @GetMapping("/all")
    public Resources<Resource<UserDomain>> all(HttpServletRequest request) {
        String appUrl = request.getScheme() + "://" + request.getServerName();
        return assembler.toResourcesUser(userService.getAll(appUrl));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDomain> one(HttpServletRequest request) {
        String appUrl = request.getScheme() + "://" + request.getServerName();
        UserDomain user = userService.toUserDomain(detailsService.getUser(),appUrl);
        System.out.println(user.getEmail());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }



    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public Resource<UserDomain> processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest  request) {
        String appUrl2 = request.getScheme() + "://" + request.getServerName();
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
            return assembler.toResource(userService.toUserDomain(optional.get(),appUrl2));
        }
    }


    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public Resource<UserDomain> displayResetPasswordPage(@PathParam("token") String token, HttpServletRequest r) {
        String appUrl2 = r.getScheme() + "://" + r.getServerName();

        Optional<ResetRequest> request = requestService.findByToken(token);
        if (request.isPresent()) {
//            todo: redirect to validPassword reset page
            System.err.println("redirecting to pass reset screen");
            return assembler.toResource(userService.toUserDomain(request.get().getUser(),appUrl2));
        } else {
            System.err.println("errorMessage : Oops!  This is an invalid validPassword reset link.");
            throw new InvalidInputException(Message.INVALID_RESET_LINK);
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Resource<UserDomain> setNewPassword(@RequestParam Map<String, String> requestParams,HttpServletRequest r) {
        String appUrl = r.getScheme() + "://" + r.getServerName();

        ResetRequest request = requestService.findByToken(requestParams.get("token")).orElseThrow(() -> new InvalidInputException(Message.TOKEN_NOT_FOUND));
        User resetUser = request.getUser();

        if (resetUser != null) {

            resetUser.setPassword(passwordService.getPasswordEncoder().encode(requestParams.get("validPassword")));
            requestService.removeRequest(request);

            userService.addUser(resetUser);
            System.err.println("successMessage: You have successfully reset your validPassword.  You may now login.");

            //TODO : redirect:login

            return assembler.toResource(userService.toUserDomain(resetUser,appUrl));

        } else {
            System.err.println("errorMessage : Oops!  This is an invalid validPassword reset link.");
            throw new InvalidInputException(Message.NOT_RECORDED_REQUEST);
        }
    }

    // TODO: redirect to login page


}
