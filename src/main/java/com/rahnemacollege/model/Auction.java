package com.rahnemacollege.model;

import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name = "auctions")
public class    Auction {

    private String title;
    private String description;
    @Min(message = "Price cannot be negative", value = 0)
    private int base_price;
    private Date date;

    @OneToMany
    private List<Picture> pictures;

    @ManyToOne
    @RestResource(exported = false)
    private Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int state;
    @ManyToOne
//    @NotNull(message = "Who is the owner of this auction?")
    private User winner;
    @ManyToOne
    private User owner;


    public Auction(){}
    public Auction(String title,String description,int base_price,List pictures,Category category,Date date){
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.pictures = pictures;
        this.date = date;
        this.category = category;
        state = 0; //0:soon 1:started 2:finished
    }



}
