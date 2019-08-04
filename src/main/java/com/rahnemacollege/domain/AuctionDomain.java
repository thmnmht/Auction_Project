package com.rahnemacollege.domain;

import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.User;
import lombok.Data;

import java.util.List;




@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int base_price;
    private String date;
    private List<Long> pictures;
    private Category category;
    private User winner;
    private User owner;

}
