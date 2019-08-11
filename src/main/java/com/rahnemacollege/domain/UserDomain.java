package com.rahnemacollege.domain;

import lombok.Data;


@Data
public class UserDomain {

    private String name;
    private String email;
    private String password;

    public UserDomain() {
    }

    public UserDomain(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
