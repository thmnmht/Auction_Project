package com.rahnemacollege.model;

import lombok.Data;
import javax.persistence.*;

import javax.validation.constraints.Email;
import java.util.Set;


@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    @Email
    private String email;
    private String password;
    private String picture;

    @ManyToMany
    private Set<Auction> bookmarks;

    @OneToMany
    private Set<Bid> bids;


}
