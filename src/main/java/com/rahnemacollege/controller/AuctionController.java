package com.rahnemacollege.controller;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;


@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;
    private UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final Logger log;
    public AuctionController(AuctionService auctionService, ResourceAssembler assembler, UserDetailsServiceImpl userDetailsService, UserService userService) {
        this.auctionService = auctionService;
        this.assembler = assembler;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        log = LoggerFactory.getLogger(AuctionController.class);
    }

    @GetMapping("/category")
    public ResponseEntity<List<Category>> getCategory() {
        log(" call get category");
        return new ResponseEntity<>(auctionService.getCategory(), HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Resource<AuctionDomain> add(@PathParam("title") String title,
                                       @PathParam("description") String description,
                                       @PathParam("base_price") int base_price,
                                       @PathParam("date") long date,
                                       @PathParam("category_id") int category_id,
                                       @PathParam("max_number") int max_number,
                                       @RequestPart MultipartFile[] images,
                                       HttpServletRequest request) throws IOException {
        log(" call add an auction");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        AuctionDomain auctionDomain = new AuctionDomain(title, description, base_price, date, category_id, max_number);
        return assembler.toResource(auctionService.addAuction(auctionDomain, images,appUrl),request);
    }

    @GetMapping("/find/{id}")
    public Resource<AuctionDomain> one(@PathVariable int id, HttpServletRequest request) {
        log(" try to find an auction");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        return assembler.toResource(auctionService.toAuctionDomain(auctionService.findById(id),appUrl),request);
    }

    @RequestMapping(value = "/addBookmark", method = RequestMethod.POST)
    public Resource<AuctionDomain> addBookmark(@RequestParam("auctionId") Integer id,HttpServletRequest request) {
        log.info(userDetailsService.getUser().getName() + " try to add bookmark");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        User user = userDetailsService.getUser();
        if (id != null) {
            if (auctionService.findById(id)!= null){
                user.getBookmarks().add(auctionService.findById(id));
                userService.addUser(user);
                return assembler.toResource(auctionService.toAuctionDomain(auctionService.findById(id),appUrl),request);
            }
            throw new InvalidInputException(Message.REALLY_BAD_SITUATION);
        }
        throw new InvalidInputException(Message.INVALID_ID);
    }


    //TODO : delete it
    @GetMapping("/all")
    public Resources<Resource<AuctionDomain>> all(HttpServletRequest request) {
        log(" try to get all auctions");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        return assembler.toResourcesAuc(auctionService.getAll(appUrl),request);
    }


    private void log(String action){
        log.info(userDetailsService.getUser().getName() + " with id " + userDetailsService.getUser().getId() + action);
    }

}


