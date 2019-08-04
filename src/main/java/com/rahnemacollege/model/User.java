package com.rahnemacollege.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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

    @OneToMany
    private Set<LoginInfo> loginInfos;



}
