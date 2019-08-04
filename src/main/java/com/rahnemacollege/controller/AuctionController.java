package com.rahnemacollege.controller;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.resources.AuctionResource;
import com.rahnemacollege.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequestMapping("/auctions")
@RestController
public class AuctionController {

    @Autowired
    private AuctionService auctionService;


    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public AuctionResource add(@RequestBody AuctionDomain auctionDomain, @RequestPart MultipartFile[] images) throws IOException {
        //set photo for auction
        auctionDomain.setPictures(auctionService.makePictures(images));

        return auctionService.addAuction(auctionDomain);
    }

}
