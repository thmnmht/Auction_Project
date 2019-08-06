package com.rahnemacollege.model;

import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.security.PublicKey;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @Size(min = 4, max = 100, message = "Invalid name")
    private String name;
    @Email(message = "Invalid email address.")
    private String email;
    @Size(min = 6, max = 100, message = "Invalid name")
    private String password;
    private String picture;

    @ManyToMany
    @RestResource(exported = false)
    private Set<Auction> bookmarks;

//    @OneToMany
//    private Set<Bid> bids;
//
//    @OneToMany
//    private Set<LoginInfo> loginInfos;


    public User(){}
    public User(String name,String email,String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
