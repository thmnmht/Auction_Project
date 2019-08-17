package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.domain.AuthenticationRequest;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.service.PasswordService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.util.JwtTokenUtil;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ResourceAssembler assembler;
    private final PasswordService passwordService;
    private TokenUtil tokenUtil;
    private UserDetailsServiceImpl detailsService;
    private AuctionService auctionService;

    public UserController(UserService userService, ResourceAssembler assembler, PasswordService passwordService,
                          JwtTokenUtil tokenUtil,
                          UserDetailsServiceImpl userDetailService,
                          AuctionService auctionService) {
        this.userService = userService;
        this.assembler = assembler;
        this.passwordService = passwordService;
        this.tokenUtil = tokenUtil;
        this.detailsService = userDetailService;
        this.auctionService = auctionService;
    }


    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        if (!userService.isExist(authenticationRequest.getEmail()))
            throw new InvalidInputException(Message.EMAIL_NOT_FOUND);
        userService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = detailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = tokenUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    @PostMapping("/edit")
    public ResponseEntity<UserDomain> edit(String name, String email) {
        UserDomain userDomain = userService.edit(name, email);
        return new ResponseEntity<>(userDomain, HttpStatus.OK);
    }

    //TODO
    @PostMapping("/profilepic")
    public Resource setPicture(@PathParam("picture") MultipartFile picture) {
        return null;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Resource<UserDomain> setNewPassword(@RequestParam("validPassword") String password) {
        User user = detailsService.getUser();
        if (password != null && password.length() > 5 && password.length() < 100) {
            user.setPassword(passwordService.getPasswordEncoder().encode(password));
            UserDomain userDomain = userService.addUser(user.getName(), user.getEmail(), user.getPassword());

            return assembler.toResource(userDomain);
        }
        throw new InvalidInputException(Message.PASSWORD_INCORRECT);
    }


    @PostMapping("/signup")
    public Resource<UserDomain> add(@PathParam("name") String name, @PathParam("emil") String email,
                                    @PathParam("validPassword") String password) {
        UserDomain user = userService.addUser(name, email, password);
        return assembler.toResource(user);
    }


    //TODO : remove it! it's just for test
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

    @GetMapping("/auctions")
    public Resources<Resource<AuctionDomain>> allUserAuctions(@RequestParam("page") int page,
                                                              @RequestParam("size") int size,
                                                              PagedResourcesAssembler<AuctionDomain> assembler) {
        User user = detailsService.getUser();
        return assembler.toResource(auctionService.findByOwner(user, page, size));
    }


    @GetMapping("/bookmarks")
    public Resources<Resource<AuctionDomain>> userBookmarks(@RequestParam("page") int page,
                                                            @RequestParam("size") int size,
                                                            PagedResourcesAssembler<AuctionDomain> assembler) {
        User user = detailsService.getUser();
        return assembler.toResource(userService.getUserBookmarks(user.getEmail(), page, size));
    }


    @GetMapping("/filter")
    public Resources<Resource<AuctionDomain>> filter(@PathParam("category") int[] categories_id,
                                                     @RequestParam("page") int page, @RequestParam("size") int size,
                                                     PagedResourcesAssembler<AuctionDomain> assembler) {
        return assembler.toResource(auctionService.filter(categories_id, page, size));
    }


}
