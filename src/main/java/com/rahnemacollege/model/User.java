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
    @Size(min = 1, max = 100, message = "Invalid name")
    private String name;
    @Email(message = "Invalid email address.")
    @Size(min = 5,message = "Invalid name")
    @Column(name = "email",unique = true)
    private String email;
    @Size(min = 6, max = 100, message = "Invalid name")
    private String password;
    private String picture;
    @ManyToMany
    @RestResource(exported = false)
    private Set<Auction> bookmarks;


    public User(){}
    public User(String name,String email,String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = null;
        this.bookmarks = null;

    }

}
