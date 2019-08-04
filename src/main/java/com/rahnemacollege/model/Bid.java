package com.rahnemacollege.model;


import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "auction_uid")
    @ManyToOne
    private Auction auction;

    @Column(name = "user_uid")
    @ManyToOne
    private User user;
    private int price;
    private Date date;



}
