package com.rahnemacollege.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rahnemacollege.model.Category;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.Link;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;


@Data
public class AuctionDomain {

    @Size(min = 1, message = "Invalid title")
    private String title;
    private String description;
    @Min(message = "Price cannot be negative", value = 0)
    private int base_price;
    private long date;
    private Category category;
    @Min(message = "Invalid number", value = 2)
    @Max(message = "Invalid number", value = 15)
    private int max_number;


    @JsonIgnore
    private int id;

    private List<Link> pictures;
    private int state;

    public AuctionDomain(String title,
                         String description,
                         int base_price,
                         long date,
                         Category category,
                         int max_number) {
        this.title = title;
        this.description = description;
        this.base_price = base_price;
        this.date = date;
        this.category = category;
        this.max_number = max_number;
    }

}
