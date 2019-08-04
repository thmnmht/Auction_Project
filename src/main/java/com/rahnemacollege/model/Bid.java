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


    @ManyToOne
//    @Column(name = "auction_uid")
    private Auction auction;


    @ManyToOne
//    @Column(name = "user_uid")
    private User user;
    private int price;
    private Date date;



}
