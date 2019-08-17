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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    private final PictureService pictureService;
    private final TokenUtil tokenUtil;
    private final Logger log;
    @Value("${server_ip}")
    private String ip;


    public UserController(UserService userService, ResourceAssembler assembler, PasswordService passwordService,
                          UserDetailsServiceImpl userDetailService,
                          UserDetailsServiceImpl detailsService,
                          ResetRequestService requestService,
                          EmailService emailService, PictureService pictureService, TokenUtil tokenUtil) {
        this.userService = userService;
        this.assembler = assembler;
        this.passwordService = passwordService;
        this.detailsService = userDetailService;
        this.detailsService = detailsService;
        this.requestService = requestService;
        this.emailService = emailService;
        this.pictureService = pictureService;
        this.tokenUtil = tokenUtil;
        log = LoggerFactory.getLogger(UserController.class);
    }


    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        log.info(authenticationRequest.getEmail() + " wants to login *_*");
        String email = authenticationRequest.getEmail();
        String password = authenticationRequest.getPassword();
        if (!userService.isExist(email))
            throw new InvalidInputException(Message.EMAIL_NOT_FOUND);
        userService.authenticate(email, password);
        final UserDetails userDetails = detailsService.loadUserByUsername(email);
        final String token = tokenUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    @PostMapping("/edit")
    public AuthenticationResponse edit(@RequestBody SimpleUserDomain userDomain) {
        log.info(detailsService.getUser().getName() + " with id " + detailsService.getUser().getId() + " try to edit his name or email");
        String email = userDomain.getEmail();
        String name = userDomain.getName();
        if (email != null)
            email = email.toLowerCase();
        User user = userService.edit(detailsService.getUser(), name, email);
        log.info(user.getName() + " changed his/her infos :)");
        final UserDetails userDetails = detailsService.loadUserByUsername(user.getEmail());
        return new AuthenticationResponse(tokenUtil.generateToken(userDetails));
    }

    @PostMapping("/edit/picture")
    public ResponseEntity<SimpleUserDomain> setUserPicture(@RequestPart MultipartFile picture){
        log.info(detailsService.getUser().getName() + " with id " + detailsService.getUser().getId() + " try to set a profile picture");
        if(picture == null)
            throw new InvalidInputException(Message.PICTURE_NULL);
        User user = detailsService.getUser();
        return new ResponseEntity<>(pictureService.setProfilePicture(user,picture),HttpStatus.OK);
    }

    @RequestMapping(value = "/edit/password", method = RequestMethod.POST)
    public Resource<SimpleUserDomain> setNewPassword(@RequestParam("oPassword") String oPassword, @RequestParam("nPassword") String nPassword) {
        log.info(detailsService.getUser().getEmail() + " tries to change password");
        User user = detailsService.getUser();
        if (nPassword == null || oPassword == null || nPassword.length() < 6) {
            log.error("Invalid password input to be changed.");
            throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
        }
        if (!passwordService.getPasswordEncoder().matches(oPassword, user.getPassword())) {
            log.error("OldPassword doesn't match user's password");
            throw new InvalidInputException(Message.PASSWORD_INCORRECT);
        }
        log.info("Password changed for : " + detailsService.getUser().getEmail());
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


    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public Resource<UserDomain> processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request) {
        userEmail = userEmail.toLowerCase();
        log.info(userEmail + " has just requested for password recovery email.");
        Optional<User> optional = userService.findUserByEmail(userEmail);
        if (!optional.isPresent()) {
            log.error("There's not account found for " + userEmail);
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
            String appUrl = request.getScheme() + "://" + ip;
//            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            try {
                emailService.sendPassRecoveryMail(userEmail, appUrl, token);
                log.info("A password reset link has been sent to " + userEmail + " @" +
                        new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return assembler.toResource(userService.toUserDomain(optional.get()));
        }
    }


    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String displayResetPasswordPage(@PathParam("token") String token, RedirectAttributes redirectAttributes) {
        log.info("Validation check for token :\"" + token + "\" is requested.");
        Optional<ResetRequest> request = requestService.findByToken(token);
        if (request.isPresent()) {
            System.err.println("redirecting to pass reset screen");
            redirectAttributes.addAttribute("token", token);
            log.info("Redirecting to passwordReset page for token : \"" + token + "\"");
            return "redirect:/passwordReset?token="+token;
        } else {
            log.error("Token: \"" + token + "\" is invalid to reset password");
            System.err.println("errorMessage : Oops!  This is an invalid validPassword reset link.");
            throw new InvalidInputException(Message.INVALID_RESET_LINK);
        }
    }


    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Resource<UserDomain> setNewPassword(@RequestParam Map<String, String> requestParams) {
        log.info("Reset password request received for token :\"" + requestParams.get("token") + "\"");
        ResetRequest request = requestService.findByToken(requestParams.get("token")).orElseGet(() -> {
            log.error("No request recorded for token: \"" + requestParams.get("token") + "\"");
            throw new InvalidInputException(Message.TOKEN_NOT_FOUND);
        });
        User resetUser = request.getUser();
        if (resetUser != null ) {
            new Validator().validPassword(requestParams.get("validPassword"));
            resetUser.setPassword(passwordService.getPasswordEncoder().encode(requestParams.get("validPassword")));
            requestService.removeRequest(request);
            userService.addUser(resetUser);
            log.info(resetUser.getEmail()+ " successfully reset his/her password");
            //TODO : redirect:login
            return assembler.toResource(userService.toUserDomain(resetUser));
        } else {
            log.error("Invalid password reset link.");
            throw new InvalidInputException(Message.NOT_RECORDED_REQUEST);
        }
    }

}
