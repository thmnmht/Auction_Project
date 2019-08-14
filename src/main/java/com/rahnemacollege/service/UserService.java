package com.rahnemacollege.service;

import com.google.common.collect.Lists;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.SimpleUserDomain;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository repository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PictureService pictureService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final Validator validator;
    private final TokenUtil tokenUtil;
    private final String VALID_EMAIL_REGEX = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
    private final Logger logger;


    @Value("${server_ip}")
    private String ip;


    public UserService(UserRepository repository, UserDetailsServiceImpl userDetailsService, PictureService pictureService, PasswordEncoder encoder, AuthenticationManager authenticationManager, Validator validator, TokenUtil tokenUtil) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
        this.pictureService = pictureService;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.validator = validator;
        this.tokenUtil = tokenUtil;
        logger = LoggerFactory.getLogger(UserService.class);
    }


    public boolean isExist(String user_email) {
        if (repository.findByEmail(user_email).isPresent())
            return true;
        return false;
    }

    public SimpleUserDomain addUser(String name, String email, String password) {
        if (!email.matches(VALID_EMAIL_REGEX))
            throw new InvalidInputException(Message.EMAIL_INVALID);
        if(isExist(email))
            throw new InvalidInputException(Message.EMAIL_DUPLICATED);
        if(password.length() < 6)
            throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
        if(password.length() > 100)
            throw new InvalidInputException(Message.PASSWORD_TOO_HIGH);
        User user = new User(name, email, encoder.encode(password));
        repository.save(user);
        return new SimpleUserDomain(name,email);
    }

    public List<UserDomain> getAll() {
        ArrayList<UserDomain> users = new ArrayList<>();
        Lists.newArrayList(repository.findAll()).stream().map(user -> toUserDomain(user)).forEach(users::add);
        return users;
    }

    public void authenticate(String email, String password) throws InvalidInputException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            logger.error(e.getMessage());
        } catch (BadCredentialsException e) {
            throw new InvalidInputException(Message.PASSWORD_INCORRECT);
        }
    }

    //??
    public User addUser(User user) {
        repository.save(user);
        return user;
    }

    //?
    public Optional<User> findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public SimpleUserDomain changePassword(User user, String newPassword) {
        if (userDetailsService.getUser().equals(user)) {
            user.setPassword(encoder.encode(newPassword));
            repository.save(user);
            return new SimpleUserDomain(user.getName(),user.getEmail());
        } else {
            throw new InvalidInputException(Message.FORBIDDEN_REQUEST);
        }
    }

    public User edit(String name, String email) throws InvalidInputException {
        User user = userDetailsService.getUser();
        if (!validator.isEmpty(email) && !user.getEmail().equals(email)){
            validator.validEmail(email);
            user.setEmail(email);
        }
        if (!validator.isEmpty(name))
            user.setName(name);
        repository.save(user);
        return user;
    }

    public AuthenticationResponse auth(String email,String password) throws InvalidInputException{
        if (!isExist(email))
            throw new InvalidInputException(Message.EMAIL_NOT_FOUND);
        authenticate(email, password);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = tokenUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    public UserDomain toUserDomain(User user) {
        UserDomain userDomain = new UserDomain(user.getName(), user.getEmail(), user.getId(), "http://" + ip + user.getPicture());
        return userDomain;
    }


    private String savePicture(MultipartFile picture) throws IOException {
        int userId = userDetailsService.getUser().getId();
        new File("./images/profile_images/" + userId + "/").mkdirs();
        String fileName = new Date().getTime() + ".jpg";
        String pathName = "./images/profile_images/" + userId + "/" + fileName;
        pictureService.save(picture, pathName);
        return pathName.substring(8);
    }

    public SimpleUserDomain setPicture(MultipartFile picture) {
        if(picture == null)
            throw new InvalidInputException(Message.PICTURE_NULL);
        User user = userDetailsService.getUser();
        try {
            user.setPicture(savePicture(picture));
            repository.save(user);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return new SimpleUserDomain(user.getName(),user.getEmail());
    }


}
