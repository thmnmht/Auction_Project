package com.rahnemacollege.domain;


import lombok.Data;
import java.util.List;


@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int base_price = -1;
    private long date = -1;
    private int category_id;
    private int max_number = -1;
    private boolean isMine = false;
    private int id;

    private List<String> pictures;

    public AuctionDomain(String title,
                         String description,
                         int base_price,
                         long date,
                         int category_id,
                         int max_number) {
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.date = date;
        this.category_id = category_id;
        this.max_number = max_number;
    }

}
