package com.rahnemacollege.service;

import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
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
        User user = toUser(userDomain);
        user.setPassword(passwordEncoder.encode(userDomain.getPassword()));
        repository.save(user);
        return user;
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
