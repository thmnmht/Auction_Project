package com.rahnemacollege.model;

import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


@Data
@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Size(min = 1, max = 100, message = "Invalid title")
    private String title;
    @Size(min = 1, max = 100, message = "Invalid description")
    private String description;
    @Min(message = "Price cannot be negative", value = 1)
    private int base_price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @RestResource(exported = false)
    @NotNull(message = "category must be selected! -_-")
    private Category category;
    @NotNull(message = "date must be selected! -_-")
    private Date date;
    private int state;
    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Min(message = "Invalid number", value = 2)
    @Max(message = "Invalid number", value = 15)
    private int max_number;


    public Auction(){}
    public Auction(String title, String description, int base_price, Category category, Date date, int max_number){
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.date = date;
        this.category = category;
        this.state = 0; //0:soon 1:started 2:finished
        this.winner = null;
        this.max_number = max_number;
    }



}
