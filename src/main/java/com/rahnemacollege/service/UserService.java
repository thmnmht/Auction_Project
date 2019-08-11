package com.rahnemacollege.service;

import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository repository;
    private final UserDetailsServiceImpl userDetailsService;

    private PasswordEncoder encoder;

    public UserService(UserRepository repository, UserDetailsServiceImpl userDetailsService, PasswordEncoder encoder) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }



    public boolean isExist(String user_email){
        if(repository.findByEmail(user_email).isPresent())
            return true;
        return false;
    }

    public User addUser(UserDomain userDomain){
        validation(userDomain);
        if(repository.findByEmail(userDomain.getEmail()).isPresent())
            throw new InvalidInputException(Message.EMAIL_DUPLICATED);
        User user = toUser(userDomain);
        repository.save(user);
        return user;
    }

    public void validation(UserDomain userDomain){
        if(userDomain.getName() == null || userDomain.getName().length() < 1)
            throw new InvalidInputException(Message.NAME_NULL);
        if(userDomain.getEmail() == null || userDomain.getEmail().length() < 5)
            throw new InvalidInputException(Message.EMAIL_NULL);
        if(!userDomain.getEmail().matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))
            throw new InvalidInputException(Message.EMAIL_INVALID);
        if(userDomain.getPassword() == null || userDomain.getPassword().length() < 6)
            throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
        if (userDomain.getPassword().length() > 100)
            throw new InvalidInputException(Message.PASSWORD_TOO_HIGH);
    }

    public List<User> getAll() {
        ArrayList<User> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        return users;
    }

    public User toUser(UserDomain userDomain){
        return new User(userDomain.getName(),userDomain.getEmail(),encoder.encode(userDomain.getPassword()));
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

    public User edit(String name,String email) throws InvalidInputException{
        if(email == null || email.length() < 1)
            email = getUser().getEmail();
        else {
            if(email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))
                throw new InvalidInputException(Message.EMAIL_INVALID);
            if(getByEmail(email).isPresent())
                throw new InvalidInputException(Message.EMAIL_DUPLICATED);
        }
        if(name == null || name.length() < 1)
            name = getUser().getName();
        User user = userDetailsService.getUser();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public User getUser(){
        User user = userDetailsService.getUser();
        return user;
    }

    public UserDomain toUserDomain(User user){
        UserDomain userDomain = new UserDomain();
        userDomain.setName(user.getName());
        userDomain.setEmail(user.getEmail());
        return userDomain;
    }


}
