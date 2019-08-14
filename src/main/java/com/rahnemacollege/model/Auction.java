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
@Table(name = "Auctions")
public class Auction {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    private String description;
    private int base_price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @RestResource(exported = false)
    private Category category;
    private Date date;
    private int state;
    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private int max_number;


    public Auction() {
    }

    public Auction(String title, String description, int base_price, Category category, Date date, User user,
                   int max_number) {
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.date = date;
        this.category = category;
        this.state = 0;
        this.winner = null;
        this.owner = user;
        this.max_number = max_number;
    }


}
