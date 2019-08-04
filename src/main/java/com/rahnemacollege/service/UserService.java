package com.rahnemacollege.service;

import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@org.springframework.stereotype.Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void addUser(User user){
        repository.save(user);
    }

    public List<User> getAll() {
        ArrayList<User> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        return users;
    }
}
