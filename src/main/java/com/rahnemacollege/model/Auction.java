package com.rahnemacollege.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity
@Table(name = "auctions")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private int id;
    private String title;
    private String description;
    private int base_price;
    private int state;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @RestResource(exported = false)
    private Category category;
    private Date date;
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
        this.winner = null;
        this.owner = user;
        this.state = 0;
        this.max_number = max_number;
    }


}
