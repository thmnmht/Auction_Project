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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Resource<AuctionDomain> add(@PathParam("title") String title,
                                       @PathParam("description") String description,
                                       @PathParam("base_price") int base_price,
                                       @PathParam("date") long date,
                                       @PathParam("category_id") int category_id,
                                       @PathParam("max_number") int max_number,
                                       @RequestPart MultipartFile[] images,
                                       HttpServletRequest request) throws IOException {
        String appUrl = request.getScheme() + "://" + request.getServerName();
        AuctionDomain auctionDomain = new AuctionDomain(title, description, base_price, date, category_id, max_number);
        return assembler.toResource(auctionService.addAuction(auctionDomain, images,appUrl));
    }

    @GetMapping("/find/{id}")
    public Resource<AuctionDomain> one(@PathVariable int id, HttpServletRequest request) {
        String appUrl = request.getScheme() + "://" + request.getServerName();
        return assembler.toResource(auctionService.toAuctionDomain(auctionService.findById(id),appUrl));
    }

    @RequestMapping(value = "/addBookmark", method = RequestMethod.POST)
    public Resource<AuctionDomain> addBookmark(@RequestParam("auctionId") Integer id,HttpServletRequest request) {
        String appUrl = request.getScheme() + "://" + request.getServerName();
        User user = userDetailsService.getUser();
        if (id != null) {
            if (auctionService.findById(id)!= null){
                user.getBookmarks().add(auctionService.findById(id));
                userService.addUser(user);
                return assembler.toResource(auctionService.toAuctionDomain(auctionService.findById(id),appUrl));
            }
            throw new InvalidInputException(Message.REALLY_BAD_SITUATION);
        }
        throw new InvalidInputException(Message.INVALID_ID);
    }


    @GetMapping("/all")
    public Resources<Resource<AuctionDomain>> all(HttpServletRequest request) {
        String appUrl = request.getScheme() + "://" + request.getServerName();
        return assembler.toResourcesAuc(auctionService.getAll(appUrl));
    }


}


