package com.rahnemacollege.domain;

import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.io.Serializable;


@Data
public class UserDomain{

    private String name;
    private String email;
    private int id;
    @RestResource(exported = false)
    private String picture;


    public UserDomain(String name,
                      String email,
                      int id,
                      String picture){
        this.name = name;
        this.email = email;
        this.id = id;
        this.picture = picture;
    }


}
