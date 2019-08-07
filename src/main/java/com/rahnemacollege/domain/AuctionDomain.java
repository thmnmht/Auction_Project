package com.rahnemacollege.domain;

import lombok.Data;
import java.util.Date;


@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int base_price;
    private Date date;
    private int category_id;
    private int max_number;

    public AuctionDomain(String title,
                         String description,
                         int base_price,
                         Date date,
                         int category_id,
                         int max_number){
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.date = date;
        this.category_id = category_id;
        this.max_number = max_number;
    }

}
