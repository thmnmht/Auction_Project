package com.rahnemacollege.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name = "auctions")
public class Auction {

    private String title;
    private String description;
    private int base_price;
    private Date date;

    @OneToMany
    @Column(name = "picture_uid")
    private List<Picture> pictures;
    @ManyToOne
    @Column(name = "category_uid")
    private Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int state;
    @ManyToOne
    private User winner;
    @ManyToOne
    private User owner;

    public Auction(String title,String description,int base_price,List pictures,Category category,Date date){
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.pictures = pictures;
        this.date = date;
        this.category = category;
        state = 0; //0:soon 1:started 2:finished
    }
//
//    @OneToMany
//    private Set<Bid> bids;



}
