package com.rahnemacollege.controller;

import com.rahnemacollege.model.User;
import com.rahnemacollege.resourceAssembler.UserResourceAssembler;
import com.rahnemacollege.resources.UserResource;
import com.rahnemacollege.service.UserService;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserResourceAssembler assembler;

    public UserController(UserService userService, UserResourceAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResource add(@RequestBody User user) {
        userService.addUser(user);
        return assembler.toResource(user);
    }

}
