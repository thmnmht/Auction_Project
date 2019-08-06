package com.rahnemacollege.domain;

import com.rahnemacollege.model.Category;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;




@Data
public class AuctionDomain {

    private MultipartFile[] pictures;
    private String title;
    private String description;
    private int base_price;
    private Category category;
    private Date date;
    private int max_number;



}
