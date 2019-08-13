package com.rahnemacollege.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    //    @Email(message = "Invalid email address.")
    @Column(name = "email", unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String picture;
    @ManyToMany
    @RestResource(exported = false)
    private Set<Auction> bookmarks;

    @OneToOne(mappedBy = "user")
    private ResetRequest resetRequest;


    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = null;
        this.bookmarks = null;
    }


}
