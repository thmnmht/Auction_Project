package com.rahnemacollege.controller;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RequestMapping("/auctions")
//@RestController
@RepositoryRestController
public class AuctionController {

    @Autowired
    private AuctionService auctionService;


    /*@PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Auction> add(@RequestBody AuctionDomain auctionDomain, @RequestPart MultipartFile[] images) throws IOException {
        //set photo for auction
        auctionDomain.setPictures(auctionService.makePictures(images));

        return new ResponseEntity<Auction>(HttpStatus.CREATED);
    }*/

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return "hello world";
    }

    /*@GetMapping("/find/{id}")
    public Resource<Auction> one(int id) {
        Auction auction = auctionService.findById(id).orElseThrow(() -> new IllegalArgumentException(id+ " was not found!"));
        return auctionService.toResource(auction);
    }

    @GetMapping("/all")
    public Resources<Resource<Auction>> all() {
        List<Resource<Auction>> auctions = auctionService.getAll().stream()
                .map(auctionService::toResource)
                .collect(Collectors.toList());
        return new Resources<>(auctions,
                linkTo(methodOn(UserController.class).all()).withSelfRel());
    }*/
}
