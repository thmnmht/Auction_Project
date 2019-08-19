package com.rahnemacollege.domain;

import lombok.Data;


@Data
public class AddAuctionDomain {
    private String title;
    private String description;
    private int basePrice;
    private long date;
    private int categoryId;
    private int maxNumber;
}
