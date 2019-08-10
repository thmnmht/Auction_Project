package com.rahnemacollege.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rahnemacollege.model.Category;
import lombok.Data;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.Link;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;


@Data
public class AuctionDomain {

    private String title;
    private String description;
    private int base_price = -1;
    private long date = -1;
    private Category category;
    private int max_number = -1;


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
