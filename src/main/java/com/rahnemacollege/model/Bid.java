package com.rahnemacollege.model;


import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    private Auction auction;
    @ManyToOne
    private User user;
    private int price;
    private Date date;



}
