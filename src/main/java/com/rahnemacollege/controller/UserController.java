package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuthenticationRequest;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.PasswordService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.util.JwtTokenUtil;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import com.rahnemacollege.util.exceptions.NotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ResourceAssembler assembler;
    private final PasswordService passwordService;
    private AuthenticationManager authenticationManager;
    private TokenUtil tokenUtil;
    private UserDetailsService userDetailsService;
    private UserDetailsServiceImpl detailsService;

    public UserController(UserService userService, ResourceAssembler assembler, PasswordService passwordService, AuthenticationManager authenticationManager,
                          JwtTokenUtil tokenUtil,
                          UserDetailsServiceImpl userDetailService, UserDetailsServiceImpl detailsService) {
        this.userService = userService;
        this.assembler = assembler;
        this.passwordService = passwordService;
        this.authenticationManager = authenticationManager;
        this.tokenUtil = tokenUtil;
        this.userDetailsService = userDetailService;
        this.detailsService = detailsService;
    }


    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        if (!userService.isExist(authenticationRequest.getEmail()))
            throw new InvalidInputException(Message.EMAIL_INCORRECT);
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = tokenUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    private void authenticate(String email, String password) throws InvalidInputException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            //???
        } catch (BadCredentialsException e) {
            throw new InvalidInputException(Message.PASSWORD_INCORRECT);
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Resource<UserDomain> setNewPassword(@RequestParam("password") String password) {
        User user = detailsService.getUser();
        if (password != null && password.length() > 5 && password.length()<100) {
            user.setPassword(passwordService.getPasswordEncoder().encode(password));
            userService.addUser(user);
            return assembler.toResource(new UserDomain(user.getName(),user.getEmail(),password));
        }
        throw new InvalidInputException(Message.PASSWORD_INCORRECT);
    }


    @PostMapping("/signup")
    public Resource<User> add(@RequestBody UserDomain userDomain) {
        User user = userService.addUser(userDomain);
        return assembler.toResource(user);
    }

    @GetMapping("/all")
    public Resources<Resource<User>> all() {
        return assembler.toResourcesUser(userService.getAll());
    }


    @GetMapping("/find/{id}")
    public Resource<User> one(@PathVariable int id) {
        @Valid User user = userService.findById(id).orElseThrow(() -> new NotFoundException(id, User.class));
        return assembler.toResource(user);
    }

}
