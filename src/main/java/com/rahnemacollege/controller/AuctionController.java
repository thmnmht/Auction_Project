package com.rahnemacollege.controller;


import com.rahnemacollege.domain.AddAuctionDomain;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.service.PictureService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.ResourceAssembler;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.slf4j.Logger;


@RestController
@RequestMapping("/auctions")

public class AuctionController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;
    private UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final PictureService pictureService;
    private final Logger log;


    public AuctionController(AuctionService auctionService, ResourceAssembler assembler, UserDetailsServiceImpl userDetailsService, UserService userService, PictureService pictureService) {
        this.auctionService = auctionService;
        this.assembler = assembler;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.pictureService = pictureService;
        log = LoggerFactory.getLogger(AuctionController.class);
    }

    @GetMapping("/category")
    public ResponseEntity<List<Category>> getCategory() {
        log(" call get category");
        return new ResponseEntity<>(auctionService.getCategory(), HttpStatus.OK);
    }

    @PostMapping(value = "/add")
    public Resource<AuctionDomain> add(@RequestBody AddAuctionDomain auctionDomain){
        log(" call add an auction");
        User user = userDetailsService.getUser();
        Auction auction = auctionService.addAuction(auctionDomain, user);
        log(" added auction");
        return assembler.toResource(auctionService.toAuctionDomain(auction, user));
    }

    @PostMapping(value = "/add/picture/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Resource<AuctionDomain> addPicture(@PathVariable int id, @RequestBody MultipartFile[] images){
        log(" try to add pictures for her/his auction.");
        Auction auction = auctionService.findById(id);
        if(images == null || images.length < 1)
            throw new InvalidInputException(Message.PICTURE_NULL);
        pictureService.setAuctionPictures(auction,images);
        log(" added picture for auction " + auction.getId());
        User user = userDetailsService.getUser();
        return assembler.toResource(auctionService.toAuctionDomain(auction, user));
    }

    @GetMapping("/find/{id}")
    public Resource<AuctionDomain> one(@PathVariable int id) {
        log(" try to find an auction");
        User user = userDetailsService.getUser();
        return assembler.toResource(auctionService.toAuctionDomain(auctionService.findById(id), user));
    }


    @RequestMapping(value = "/bookmark", method = RequestMethod.POST)
    public Resource<AuctionDomain> addBookmark(@RequestParam("auctionId") Integer id) {
        log.info(userDetailsService.getUser().getName() + " try to add bookmark");
        User user = userDetailsService.getUser();
        if (id != null) {
            if (auctionService.findAuctionById(id)!= null){
                auctionService.addBookmark(user, id);
                return assembler.toResource(auctionService.toAuctionDomain(auctionService.findAuctionById(id), user));
            }
            throw new InvalidInputException(Message.REALLY_BAD_SITUATION);
        }
        throw new InvalidInputException(Message.INVALID_ID);
    }


    //TODO : delete it
    @GetMapping("/all")
    public Resources<Resource<AuctionDomain>> all() {
        log(" try to get all auctions");
        User user = userDetailsService.getUser();
        return assembler.toResourcesAuc(auctionService.toAuctionDomainList(auctionService.getAll(), user));
    }


    private void log(String action){
        log.info(userDetailsService.getUser().getName() + " with id " + userDetailsService.getUser().getId() + action);
    }

}


