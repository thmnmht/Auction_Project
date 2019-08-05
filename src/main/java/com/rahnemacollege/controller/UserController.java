package com.rahnemacollege.controller;

import com.rahnemacollege.model.User;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.service.UserService;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ResourceAssembler assembler;

    public UserController(UserService userService, ResourceAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @PostMapping("/signup")
//    @ResponseStatus(HttpStatus.CREATED)
    public Resource<User> add(@RequestBody User user) {
        userService.addUser(user);
        return assembler.toResource(user);
    }

    @GetMapping("/all")
    public Resources<Resource<User>> all() {
        List<Resource<User>> users = userService.getAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(users,
                linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    @RequestMapping(method = GET, value = "/findAll")
    public @ResponseBody ResponseEntity<?> getAllUsers() {
        List<User> userList = userService.getAll();
        Resources<User> resources = new Resources<>(userList);
        resources.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        return ResponseEntity.ok(resources);
    }


    @GetMapping("/find/{id}")
    public Resource<User> one(int id) {
        User user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException(id + " was not found!"));
        return assembler.toResource(user);
    }


}
