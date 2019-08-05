package com.rahnemacollege.domain;

import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.Picture;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;




@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int base_price;
    private Date date;
    private Category category;
    private MultipartFile[] pictures;





}
