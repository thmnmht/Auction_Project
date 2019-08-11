package com.rahnemacollege.service;

import com.google.common.collect.Lists;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository repository;
    private final UserDetailsServiceImpl userDetailsService;


    public UserService(UserRepository repository, UserDetailsServiceImpl userDetailsService) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
    }



    public boolean isExist(String user_email){
        if(repository.findByEmail(user_email).isPresent())
            return true;
        return false;
    }

    public UserDomain addUser(String name,String email,String password){
        validation(name,email,password);
        if(repository.findByEmail(email).isPresent())
            throw new InvalidInputException(Message.EMAIL_DUPLICATED);
        User user = new User(name,email,password);
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
    }

    public List<UserDomain> getAll() {
        ArrayList<UserDomain> users = new ArrayList<>();
        Lists.newArrayList(repository.findAll()).stream().map(user -> toUserDomain(user)).forEach(users::add);
        return users;
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
            email = getUser().getEmail();
        else {
            if(!email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))
                throw new InvalidInputException(Message.EMAIL_INVALID);
            if(getByEmail(email).isPresent())
                throw new InvalidInputException(Message.EMAIL_DUPLICATED);
        }
        if(name == null || name.length() < 1)
            name = getUser().getName();
        User user = userDetailsService.getUser();
        user.setName(name);
        user.setEmail(email);
        return toUserDomain(user);
    }

    public UserDomain getUser(){
        User user = userDetailsService.getUser();
        return toUserDomain(user);
    }

    public UserDomain toUserDomain(User user){
        UserDomain userDomain = new UserDomain(user.getName(),user.getEmail(),user.getId(),user.getPicture());
        return userDomain;
    }


}
