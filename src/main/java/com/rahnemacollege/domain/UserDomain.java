package com.rahnemacollege.domain;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDomain {

    private String name;
    @Email(message = "Invalid email address.")
    private String email;
    private String password;


}
