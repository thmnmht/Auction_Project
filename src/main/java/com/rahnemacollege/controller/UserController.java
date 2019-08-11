package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuthenticationRequest;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.util.JwtTokenUtil;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.apache.catalina.loader.ResourceEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ResourceAssembler assembler;
    private final AuthenticationManager authenticationManager;
    private final TokenUtil tokenUtil;
    private final UserDetailsService userDetailsService;

    public UserController(UserService userService, ResourceAssembler assembler, AuthenticationManager authenticationManager,
                          JwtTokenUtil tokenUtil,
                          UserDetailsServiceImpl userDetailService) {
        this.userService = userService;
        this.assembler = assembler;
        this.authenticationManager = authenticationManager;
        this.tokenUtil = tokenUtil;
        this.userDetailsService = userDetailService;
    }

    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidInputException {
        if(!userService.isExist(authenticationRequest.getEmail()))
            throw new InvalidInputException(Message.EMAIL_INCORRECT);
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = tokenUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    private void authenticate(String email, String password) throws InvalidInputException{
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            //???
        } catch (BadCredentialsException e) {
            throw new InvalidInputException(Message.PASSWORD_INCORRECT);
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<UserDomain> edit(String name,String email){
        UserDomain userDomain = userService.edit(name,email);
        return new ResponseEntity<>(userDomain,HttpStatus.OK);
    }

    @PostMapping("/signup")
    public Resource<UserDomain> add(@PathParam("name") String name,@PathParam("emil") String email,@PathParam("password") String password) {
        UserDomain user = userService.addUser(name,email,password);
        return assembler.toResource(user);
    }

    @GetMapping("/all")
    public Resources<Resource<UserDomain>> all() {
        return assembler.toResourcesUser(userService.getAll());
    }


    @GetMapping("/me")
    public ResponseEntity<UserDomain> one(){
        UserDomain user = userService.getUser();
        System.out.println(user.getEmail());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
