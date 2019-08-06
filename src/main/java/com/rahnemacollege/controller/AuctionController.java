package com.rahnemacollege.controller;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.util.ResourceAssembler;
import org.hibernate.validator.constraints.URL;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;

    public AuctionController(AuctionService auctionService, ResourceAssembler assembler) {
        this.auctionService = auctionService;
        this.assembler = assembler;
    }

    @GetMapping("/category")
    public List<Category> getCategory(){
        return auctionService.getCategory();
    }


    @PostMapping("/add")
    public Resource<Auction> add(@RequestBody AuctionDomain auctionDomain) throws IOException {
        return assembler.toResource(auctionService.addAuction(auctionDomain));
    }

    @GetMapping("/greeting")
    public String greeting() {
        return "hello world :)";
    }

    @GetMapping("/find/{id}")
    public Resource<Auction> one(@PathVariable int id) {
        Auction auction = auctionService.findById(id).orElseThrow(() -> new IllegalArgumentException(id+ " was not found!"));
        return assembler.toResource(auction);
    }

    @GetMapping("/all")
    public Resources<Resource<Auction>> all() {
        return assembler.toResourcesAuc(auctionService.getAll());
    }
}


