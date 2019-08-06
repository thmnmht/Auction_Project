package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "Auction_Pictures")
public class AuctionPicture {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "Auction_id")
    private Auction auction;
    public AuctionPicture(){
        date = new Date();
    }


}
