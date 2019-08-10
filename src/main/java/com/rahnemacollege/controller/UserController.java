package com.rahnemacollege.controller;

import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.exceptions.NotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

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
    public Resource<User> add(@RequestBody @Valid UserDomain userDomain) {
        User user = userService.addUser(userDomain);
        return assembler.toResource(user);
    }

    @GetMapping("/all")
    public Resources<Resource<User>> all() {
        return assembler.toResourcesUser(userService.getAll());
    }


    @GetMapping("/find/{id}")
    public Resource<User> one(@PathVariable int id) {
        @Valid User user = userService.findById(id).orElseThrow(() -> new NotFoundException(id ,User.class));
        return assembler.toResource(user);
    }

}
