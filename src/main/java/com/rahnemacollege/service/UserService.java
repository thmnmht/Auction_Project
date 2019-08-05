package com.rahnemacollege.service;

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

    public void addUser(User user){
        repository.save(user);
    }

    public List<User> getAll() {
        ArrayList<User> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        return users;
    }

    public Optional<com.rahnemacollege.model.User> findById(int id) {
        return repository.findById(id);
    }
}
