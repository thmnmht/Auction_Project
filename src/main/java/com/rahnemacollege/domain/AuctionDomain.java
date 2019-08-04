package com.rahnemacollege.domain;

import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.Picture;
import lombok.Data;

import java.util.Date;
import java.util.List;




@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int base_price;
    private Date date;
    private Category category;
    private List<Picture> pictures;



    public AuctionDomain(String title,String description,int base_price,Category category,Date date){
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.date = date;
        this.category = category;
    }





}
