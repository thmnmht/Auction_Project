package com.rahnemacollege.controller;

import com.rahnemacollege.model.User;
import com.rahnemacollege.resourceAssembler.UserResourceAssembler;
import com.rahnemacollege.resources.UserResource;
import com.rahnemacollege.service.UserService;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserResourceAssembler assembler;

    public UserController(UserService userService, UserResourceAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResource add(@RequestBody User user) {
        userService.addUser(user);
        return assembler.toResource(user);
    }
//
//    @GetMapping("/all")
//    public Resources<UserResource> all() {
//        List<UserResource> users = userService.getAll().stream()
//                .map(assembler::toResource)
//                .collect(Collectors.toList());
//        return new Resources<>(users,
//                linkTo(methodOn(UserController.class).all()).withSelfRel());
//    }


    @GetMapping("/all")
    public String all() {

        return "salam!!";
    }



}
