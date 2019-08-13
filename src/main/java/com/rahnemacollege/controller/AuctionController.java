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
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;
    private UserDetailsServiceImpl userDetailsService;
    private final UserService userService;


    public AuctionController(AuctionService auctionService, ResourceAssembler assembler, UserDetailsServiceImpl userDetailsService, UserService userService) {
        this.auctionService = auctionService;
        this.assembler = assembler;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @GetMapping("/category")
    public List<Category> getCategory() {
        return auctionService.getCategory();
    }


    //TODO : remove it :(
    @RequestMapping(value = "/image/{id}/{picture_fileName}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable int id, @PathVariable String picture_fileName) {
        org.springframework.core.io.Resource resource = auctionService.imageUpload(id, picture_fileName);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }


    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Resource<AuctionDomain> add(@PathParam("title") String title,
                                       @PathParam("description") String description,
                                       @PathParam("base_price") int base_price,
                                       @PathParam("date") long date,
                                       @PathParam("category_id") int category_id,
                                       @PathParam("max_number") int max_number,
                                       @RequestPart MultipartFile[] images) throws IOException {
        System.out.println("salam");
        AuctionDomain auctionDomain = new AuctionDomain(title, description, base_price, date, category_id, max_number);
        return assembler.toResource(auctionService.addAuction(auctionDomain, images));
    }

    @GetMapping("/find/{id}")
    public Resource<AuctionDomain> one(@PathVariable int id) {
        return assembler.toResource(auctionService.findById(id));
    }

    @RequestMapping(value = "/addBookmark", method = RequestMethod.POST)
    public Resource<AuctionDomain> addBookmark(@RequestParam("auctionId") Integer id) {
        User user = userDetailsService.getUser();
        if (id != null) {
            if (auctionService.findAuctionById(id)!= null){
                user.getBookmarks().add(auctionService.findAuctionById(id));
                userService.addUser(user);
                return assembler.toResource(auctionService.toAuctionDomain(auctionService.findAuctionById(id)));
            }
            throw new InvalidInputException(Message.REALLY_BAD_SITUATION);
        }
        throw new InvalidInputException(Message.INVALID_ID);
    }


    @GetMapping("/all")
    public Resources<Resource<AuctionDomain>> all() {
        return assembler.toResourcesAuc(auctionService.getAll());
    }


}


