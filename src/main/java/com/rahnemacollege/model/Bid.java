package com.rahnemacollege.model;


import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Min;
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
    private User user;
    @Min(value = 0,message = "Price could not be negative value.")
    private int price;
    private Date date;



}
