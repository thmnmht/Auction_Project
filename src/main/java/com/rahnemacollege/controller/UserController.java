package com.rahnemacollege.controller;

import com.rahnemacollege.service.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Service service;

    public UserController(Service service) {
        this.service = service;
    }

}
