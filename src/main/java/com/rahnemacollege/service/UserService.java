package com.rahnemacollege.service;

import com.google.common.collect.Lists;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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



    public UserService(UserRepository repository, UserDetailsServiceImpl userDetailsService, PictureService pictureService, PasswordEncoder encoder, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
        this.pictureService = pictureService;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }



    public boolean isExist(String user_email){
        if(repository.findByEmail(user_email).isPresent())
            return true;
        return false;
    }

    public UserDomain addUser(String name,String email,String password){
        validation(name,email,password);
        User user = new User(name,email,encoder.encode(password));
        repository.save(user);
        return toUserDomain(user);
    }

    public void validation(String name,String email,String password){
        if(name == null || name.length() < 1)
            throw new InvalidInputException(Message.NAME_NULL);
        if(email == null || email.length() < 5)
            throw new InvalidInputException(Message.EMAIL_NULL);
        if(!email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))
            throw new InvalidInputException(Message.EMAIL_INVALID);
        if(password == null || password.length() < 6)
            throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
        if (password.length() > 100)
            throw new InvalidInputException(Message.PASSWORD_TOO_HIGH);
        if(repository.findByEmail(email).isPresent())
            throw new InvalidInputException(Message.EMAIL_DUPLICATED);
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
            //???
        } catch (BadCredentialsException e) {
            throw new InvalidInputException(Message.PASSWORD_INCORRECT);
        }
    }

    public Optional<User> getByEmail(String email) {
        return repository.getByEmail(email);
    }

    public User addUser(User user) {
        repository.save(user);
        return user;
    }

    public Optional<User> findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public UserDomain edit(String name,String email) throws InvalidInputException{
        if(email == null || email.length() < 1)
            email = userDetailsService.getUser().getEmail();
        else {
            if(!email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))
                throw new InvalidInputException(Message.EMAIL_INVALID);
            if(getByEmail(email).isPresent())
                throw new InvalidInputException(Message.EMAIL_DUPLICATED);
        }
        if(name == null || name.length() < 1)
            name = userDetailsService.getUser().getName();
        User user = userDetailsService.getUser();
        user.setName(name);
        user.setEmail(email);
        repository.save(user);
        return toUserDomain(user);
    }

    public UserDomain toUserDomain(User user){
        UserDomain userDomain = new UserDomain(user.getName(),user.getEmail(),user.getId(),user.getPicture());
        return userDomain;
    }

    public String savePicture(MultipartFile picture) throws IOException {

        int userId = userDetailsService.getUser().getId();
        new File("./images/profile_images/" + userId + "/" ).mkdirs();
            String fileName = userId + "_" + new Date().getTime() + ".jpg";
            String pathName = "./images/auction_images/" + userId + "/" +  fileName;
            pictureService.save(picture,pathName);
        return pathName;
    }

    //TODO
    public void setPicture(MultipartFile picture){

    }


}
