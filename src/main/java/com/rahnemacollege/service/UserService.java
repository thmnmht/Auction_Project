package com.rahnemacollege.service;

import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User addUser(UserDomain userDomain){
        User user = toUser(userDomain);
        repository.save(user);
        return user;
    }

    public List<User> getAll() {
        ArrayList<User> users = new ArrayList<>();
        repository.findAll().forEach(u -> users.add(u));
        return users;
    }

    public User toUser(UserDomain userDomain){
        return new User(userDomain.getName(),userDomain.getEmail(),userDomain.getPassword());
    }
    public Optional<com.rahnemacollege.model.User> findById(int id) {
        return repository.findById(id);
    }
}
