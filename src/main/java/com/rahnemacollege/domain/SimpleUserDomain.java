package com.rahnemacollege.domain;


import lombok.Data;

@Data
public class SimpleUserDomain {
    private String name;
    private String email;
    private int id;

    public SimpleUserDomain(String name,String email,int id){
        this.name = name;
        this.email = email;
        this.id = id;
    }
}
