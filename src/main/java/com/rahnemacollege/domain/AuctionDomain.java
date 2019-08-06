package com.rahnemacollege.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;




@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int base_price;
    private Date date;
    private int category_id;
    private MultipartFile[] pictures;



}
