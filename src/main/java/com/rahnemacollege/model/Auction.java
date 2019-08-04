package com.rahnemacollege.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Data
@Entity
public class Auction {

    private String title;
    private String description;
    private int base_price;
    private Date date;

    @OneToMany
    private List<Picture> pictures;
    @ManyToOne
    private Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int state;
    private User winner;
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

    @Version
    private Integer version;

    @OneToMany
    private Set<Bid> bids;



}
