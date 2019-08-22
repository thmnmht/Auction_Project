package com.rahnemacollege.domain;


import lombok.Data;

import java.util.List;


@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int basePrice = -1;
    private long date = -1;
    private int categoryId;
    private int maxNumber = -1;
    private boolean bookmark = false;
    private boolean mine = false;
    private int id;

    private List<String> pictures;



    public AuctionDomain(String title,
                         String description,
                         int basePrice,
                         long date,
                         int categoryId,
                         int maxNumber) {
        this.title = title;
        this.description = description;
        this.basePrice = basePrice;
        this.date = date;
        this.categoryId = categoryId;
        this.maxNumber = maxNumber;
    }

}
