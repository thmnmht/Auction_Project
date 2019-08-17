package com.rahnemacollege.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class AddAuctionDomain {
    private String title;
    private String description;
    private int base_price;
    private long date;
    private int category_id;
    private int max_number;
}
