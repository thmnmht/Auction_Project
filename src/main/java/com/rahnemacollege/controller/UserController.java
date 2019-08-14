package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuthenticationRequest;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.ResetRequest;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.*;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
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
        this.validator = validator;
    }


    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        if (!userService.isExist(authenticationRequest.getEmail()))
            throw new InvalidInputException(Message.EMAIL_INCORRECT);
        userService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = tokenUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }


    @RequestMapping(value = "/edit_password", method = RequestMethod.POST)
    public Resource<UserDomain> setNewPassword(@RequestParam("oPassword") String oPassword, @RequestParam("nPassword") String nPassword) {
        User user = detailsService.getUser();
        validator.validPassword(nPassword);
        validator.validPassword(oPassword);
        if (passwordService.getPasswordEncoder().encode(oPassword).equals(user.getPassword())) {
            return assembler.toResource(userService.changePassword(user, nPassword));
        }
        throw new InvalidInputException(Message.PASSWORD_INCORRECT);
    }


    @PostMapping("/signup")
    public Resource<User> add(@RequestBody UserDomain userDomain) {
        System.out.println("salm");
        User user = userService.addUser(userDomain);
        System.out.println("hale");
        return assembler.toResource(user);
    }

    @GetMapping("/all")
    public Resources<Resource<UserDomain>> all() {
        return assembler.toResourcesUser(userService.getAll());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDomain> one() {
        UserDomain user = userService.toUserDomain(detailsService.getUser());
        System.out.println(user.getEmail());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public Resource<UserDomain> processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request) {
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


    // TODO: redirect to login page if something went wrong

}
