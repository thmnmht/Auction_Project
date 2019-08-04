package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
public class Auction {

    private String title;
    private String description;
    private int base_price;
    private String date;
    private List<Long> pictures;
    private Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private State state = State.SOON;
    private User winner;
    private User owner;
    @Version
    private Integer version;



}
