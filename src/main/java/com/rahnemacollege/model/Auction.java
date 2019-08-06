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
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    private String description;
    @Min(message = "Price cannot be negative", value = 0)
    private int base_price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @RestResource(exported = false)
    private Category category;
    private Date date;
    private int state;
    @ManyToOne
    @JoinColumn(name = "winner_id")
//    @NotNull(message = "Who is the owner of this auction?")
    private User winner;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private int max_number;


    public Auction(){}
    public Auction(String title, String description, int base_price, Category category, Date date, int max_number){
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.date = date;
        this.category = category;
        this.state = 0; //0:soon 1:started 2:finished
        this.max_number = max_number;
    }



}
