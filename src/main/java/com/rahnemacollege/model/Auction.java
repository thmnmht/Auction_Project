package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@Entity
public class Auction {

    private String title;
    private String description;
    private int base_price;
    private Date date;
    private List<Picture> pictures;
    private int category_id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private State state;
    private User winner;
    private User owner;


//    @Version
//    private Integer version;


    public Auction(String title,String description,int base_price,List pictures,int category_id,Date date){
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.pictures = pictures;
        this.date = date;
        this.category_id = category_id;
        state = State.SOON;
    }



}
