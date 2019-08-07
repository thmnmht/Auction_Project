package com.rahnemacollege.domain;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;


@Data
public class AuctionDomain {

    @Size(min = 1, message = "Invalid title")
    private String title;
    private String description;
    @Min(message = "Price cannot be negative", value = 0)
    private int base_price;
    private Date date;
    private int category_id;
    @Min(message = "Invalid number", value = 2)
    @Max(message = "Invalid number", value = 15)
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
