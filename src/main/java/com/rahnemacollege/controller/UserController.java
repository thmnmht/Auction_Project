package com.rahnemacollege.controller;

import com.rahnemacollege.domain.*;
import com.rahnemacollege.model.Auction;
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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final AuctionService auctionService;
    private final BidService bidService;

    private final Logger log;
    @Value("${server_ip}")
    private String ip;


    public UserController(UserService userService, ResourceAssembler assembler, PasswordService passwordService,
                          UserDetailsServiceImpl userDetailService,
                          UserDetailsServiceImpl detailsService,
                          ResetRequestService requestService,
                          EmailService emailService, PictureService pictureService, TokenUtil tokenUtil, AuctionService auctionService, BidService bidService) {
        this.userService = userService;
        this.assembler = assembler;
        this.passwordService = passwordService;
        this.detailsService = userDetailService;
        this.detailsService = detailsService;
        this.requestService = requestService;
        this.emailService = emailService;
        this.pictureService = pictureService;
        this.tokenUtil = tokenUtil;
        this.auctionService = auctionService;
        this.bidService = bidService;
        log = LoggerFactory.getLogger(UserController.class);
    }


    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        log.info(authenticationRequest.getEmail() + " wants to login *_*");
        String password = authenticationRequest.getPassword();
        int id  = userService.findUserByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new InvalidInputException(Message.EMAIL_NOT_FOUND))
                .getId();
        userService.authenticate(id, password);
        final UserDetails userDetails = detailsService.loadUserByUsername(String.valueOf(id));
        final String token = tokenUtil.generateToken(userDetails);
        log.info("the token will expired : " + tokenUtil.getExpirationDateFromToken(token));
        return new AuthenticationResponse(token);
    }

    @PostMapping("/edit")
    public ResponseEntity<SimpleUserDomain> edit(@RequestBody SimpleUserDomain userDomain) {
        log.info(detailsService.getUser().getName() + " with id " + detailsService.getUser().getId() + " try to edit his name or email");
        String email = userDomain.getEmail();
        String name = userDomain.getName();
        if (email != null)
            email = email.toLowerCase();
        User user = userService.edit(detailsService.getUser(), name, email);
        log.info(user.getName() + " changed his/her infos :)");
        SimpleUserDomain simpleUserDomain = new SimpleUserDomain(user.getName(),user.getEmail());
        return new ResponseEntity<>(simpleUserDomain,HttpStatus.OK);
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
    public ResponseEntity<SimpleUserDomain> setNewPassword(@RequestParam("oPassword") String oPassword, @RequestParam("nPassword") String nPassword) {
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
        return new ResponseEntity<>(userService.changePassword(user, nPassword), HttpStatus.OK);
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

    @GetMapping("/auctions")
    public PagedResources<Resource<AuctionDomain>> allUserAuctions(@RequestParam("page") int page,
                                                                             @RequestParam("size") int size,
                                                                             PagedResourcesAssembler<AuctionDomain> assembler) {
        //TODO : change it
        User user = detailsService.getUser();
        List<Auction> auctions = auctionService.findByOwner(user);
        List<AuctionDomain> auctionDomains = auctions.stream().map(a ->
                auctionService.toAuctionDomain(a,user,bidService.getMembers(a))).collect(Collectors.toList());
        return assembler.toResource(auctionService.toPage(auctionDomains, page, size));
    }


    @GetMapping("/bookmarks")
    public PagedResources<Resource<AuctionDomain>> userBookmarks(@RequestParam("page") int page,
                                                            @RequestParam("size") int size,
                                                            PagedResourcesAssembler<AuctionDomain> assembler) {
        User user = detailsService.getUser();
        List<Auction> auctions = userService.getUserBookmarks(user);
        List<AuctionDomain> auctionDomains = auctions.stream().map(a ->
                auctionService.toAuctionDomain(a,user,bidService.getMembers(a))).collect(Collectors.toList());
        return assembler.toResource(auctionService.toPage(auctionDomains, page, size));
    }


    /*@GetMapping("/filter")
    public Resources<Resource<AuctionDomain>> filter(@PathParam("category") int[] categories_id,
                                                     @RequestParam("page") int page, @RequestParam("size") int size,
                                                     PagedResourcesAssembler<AuctionDomain> assembler) {
        return assembler.toResource(auctionService.filter(categories_id, page, size));
    }*/


    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public ResponseEntity<SimpleUserDomain> processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request) {
        userEmail = userEmail.toLowerCase();
        log.info(userEmail + " has just requested for password recovery email.");
        Optional<User> optional = userService.findUserByEmail(userEmail);
        if (!optional.isPresent()) {
            log.error("There's not account found for " + userEmail);
            throw new InvalidInputException(Message.EMAIL_NOT_FOUND);
        } else {
            User user = optional.get();
            String appUrl = request.getScheme() + "://" + ip;
//            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            try {
                emailService.sendPassRecoveryMail(userEmail, appUrl, requestService.registerResetRequest(user));
                log.info("A password reset link has been sent to " + userEmail + " @" +
                        new Date());
            } catch (Exception e) {
                log.error("Failed to send email to " + userEmail + " ," + e);
            }
            return new ResponseEntity<>(new SimpleUserDomain(user.getName(), userEmail), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public ResponseEntity<SimpleUserDomain> setNewPassword(@RequestParam Map<String, String> requestParams) {
        String token = requestParams.get("token"), password = requestParams.get("validPassword");
        log.info("Reset password request received for token :\"" + token + "\"");
        ResetRequest request = requestService.findByToken(token).orElseGet(() -> {
            log.error("No request recorded for token: \"" + token + "\"");
            throw new InvalidInputException(Message.TOKEN_NOT_FOUND);
        });
        User resetUser = request.getUser();
        if (resetUser != null ) {
            if (password == null || password.length() < 6)
                throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
            if (password.length() > 100)
                throw new InvalidInputException(Message.PASSWORD_TOO_HIGH);
            requestService.removeRequest(request);
            log.info(resetUser.getEmail()+ " successfully reset his/her password");
            return new ResponseEntity<>(userService.changePassword(resetUser, password), HttpStatus.OK);
        } else {
            log.error("Invalid password reset link.");
            throw new InvalidInputException(Message.NOT_RECORDED_REQUEST);
        }
    }

}
