package com.rahnemacollege.service;

import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;


@org.springframework.stereotype.Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void addUser(User user){
        repository.save(user);
    }
}
