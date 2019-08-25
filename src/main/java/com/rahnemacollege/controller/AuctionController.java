package com.rahnemacollege.controller;


import com.rahnemacollege.domain.AddAuctionDomain;
import com.rahnemacollege.domain.AuctionDetail;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.*;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/auctions")

public class AuctionController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;
    private final UserService userService;
    private final PictureService pictureService;
    private final BidService bidService;
    private final Logger log;
    private UserDetailsServiceImpl userDetailsService;


    public AuctionController(AuctionService auctionService, ResourceAssembler assembler, UserDetailsServiceImpl userDetailsService, UserService userService, PictureService pictureService, BidService bidService) {
        this.auctionService = auctionService;
        this.assembler = assembler;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.pictureService = pictureService;
        this.bidService = bidService;
        log = LoggerFactory.getLogger(AuctionController.class);
    }


    @GetMapping("/bid")
    public String temp() {
        return "bid.html";
    }

    @GetMapping("/category")
    public ResponseEntity<List<Category>> getCategory() {
        log(" call get category");
        return new ResponseEntity<>(auctionService.getCategory(), HttpStatus.OK);
    }

    @PostMapping(value = "/add")
    public Resource<AddAuctionDomain> add(@RequestBody AddAuctionDomain auctionDomain) {
        log(" call add an auction");
        User user = userDetailsService.getUser();
        Auction auction = auctionService.addAuction(auctionDomain, user);
        log(" added auction");
        AddAuctionDomain addAuctionDomain = new AddAuctionDomain(auction);
        return assembler.toResource(addAuctionDomain);
    }

    @PostMapping(value = "/add/picture/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Resource<AddAuctionDomain> addPicture(@PathVariable int id, @RequestBody MultipartFile[] images) {
        log(" try to add pictures for her/his auction.");
        Auction auction = auctionService.findById(id);
        if (images == null || images.length < 1)
            throw new InvalidInputException(Message.PICTURE_NULL);
        pictureService.setAuctionPictures(auction, images);
        log(" added picture for auction " + auction.getId());
        AddAuctionDomain addAuctionDomain = new AddAuctionDomain(auction);
        return assembler.toResource(addAuctionDomain);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<AuctionDetail> one(@PathVariable int id) {
        log(" try to find an auction");
        User user = userDetailsService.getUser();
        Auction auction = auctionService.findById(id);
        log(" find the auction with title " + auction.getTitle());
        int lastPrice = bidService.findLastPrice(auction);
        int members = bidService.getMembers(auction);
        AuctionDomain auctionDomain = auctionService.toAuctionDomain(auction, user, members);
        System.out.println("to auctionDomain ");
        return new ResponseEntity<>(new AuctionDetail(auctionDomain, auction.getDescription(), auction.getBasePrice(), lastPrice), HttpStatus.OK);
    }


    @RequestMapping(value = "/bookmark", method = RequestMethod.POST)
    public Resource<AddAuctionDomain> addBookmark(@RequestParam("auctionId") Integer id) {
        User user = userService.findUserId(userDetailsService.getUser().getId()).get();
        log.info(user.getEmail() + " tried to add bookmark");
        if (id != null) {
            Auction auction= auctionService.findAuctionById(id);
            if (auction != null) {
                auctionService.addBookmark(user, auction);
                AddAuctionDomain addAuctionDomain = new AddAuctionDomain(auction);
                log.info("Auction Id#"+auction.getId()+ " just added to "+user.getEmail()+"'s bookmarks");
                return assembler.toResource(addAuctionDomain);
            }
            throw new InvalidInputException(Message.REALLY_BAD_SITUATION);
        }
        throw new InvalidInputException(Message.INVALID_ID);
    }


    private void log(String action) {
        log.info(userDetailsService.getUser().getName() + " with id " + userDetailsService.getUser().getId() + action);
    }

}



