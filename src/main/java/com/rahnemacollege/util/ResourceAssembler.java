package com.rahnemacollege.util;

import com.rahnemacollege.controller.AuctionController;
import com.rahnemacollege.controller.UserController;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ResourceAssembler {

    public Resource<User> toResource(User user){
        return new Resource<>(user,
                linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).all()).withRel("all"));
    }

    public Resources<Resource<User>> toResourcesUser(List<User> users){
       return new Resources<>(users.stream()
                .map(this::toResource)
                .collect(Collectors.toList()),linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    public Resources<Resource<Auction>> toResourcesAuc(List<Auction> auctions){
       return new Resources<>(auctions.stream()
                .map(this::toResource)
                .collect(Collectors.toList()),linkTo(methodOn(AuctionController.class).all()).withSelfRel());
    }

    public Resource<Auction> toResource(Auction auction){
        return new Resource<>(auction,
                linkTo(methodOn(AuctionController.class).all()).withRel("all"),
                linkTo(methodOn(AuctionController.class).one(auction.getId())).withRel("self"));
    }

}
