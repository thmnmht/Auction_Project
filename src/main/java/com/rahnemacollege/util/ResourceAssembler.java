package com.rahnemacollege.util;

import com.rahnemacollege.controller.AuctionController;
import com.rahnemacollege.controller.UserController;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ResourceAssembler {

    public Resource<User> toResource(User user){
        return new Resource<>(user,
                linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).all()).withRel("all"));
    }

    public Resource<Auction> toResource(Auction auction){
        return null;//new Resource<>(auction,
                //linkTo(methodOn(AuctionController.class).one(auction.getId())).withSelfRel(),
                //linkTo(methodOn(AuctionController.class).all()).withRel("all"));
    }

}
