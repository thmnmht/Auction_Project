package com.rahnemacollege.service;

import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(UserDomain userDomain){
        if(repository.findByEmail(userDomain.getEmail()).isPresent())
            throw new InvalidInputException(Message.EMAIL_DUPLICATED);
        User user = toUser(userDomain);
        user.setPassword(passwordEncoder.encode(userDomain.getPassword()));
        repository.save(user);
        return user;
    }

    public void validation(UserDomain userDomain){
        if(userDomain.getName() == null || userDomain.getName().length() < 1)
            throw new InvalidInputException(Message.NAME_NULL);
        if(userDomain.getEmail() == null || userDomain.getEmail().length() < 5)
            throw new InvalidInputException(Message.EMAIL_NULL);
        if(userDomain.getEmail().matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$"))
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
        return new User(userDomain.getName(),userDomain.getEmail(),passwordEncoder.encode(userDomain.getPassword()));
    }
    public Optional<com.rahnemacollege.model.User> findById(int id) {
        return repository.findById(id);
    }

    public User addUser(User user) {
        repository.save(user);
        return user;
    }

    public Optional<User> findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findUserByResetToken(String token) {
        return repository.findUserByResetToken(token);
    }
}
