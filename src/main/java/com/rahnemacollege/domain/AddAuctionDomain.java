package com.rahnemacollege.domain;

import com.rahnemacollege.model.Auction;
import lombok.Data;


@Data
public class AddAuctionDomain {
    private String title;
    private String description;
    private int basePrice;
    private long date;
    private int categoryId;
    private int maxNumber;

    public AddAuctionDomain(Auction auction){
        title = auction.getTitle();
        description = auction.getDescription();
        basePrice = auction.getBasePrice();
         date = auction.getDate().getTime();
        categoryId = auction.getCategory().getId();
        maxNumber = auction.getMaxNumber();
    }

}
