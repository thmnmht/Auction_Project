package com.rahnemacollege.resources;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;


@Data
public class AuctionResource extends ResourceSupport {

    String title;
    String description;
    Date date;



}
