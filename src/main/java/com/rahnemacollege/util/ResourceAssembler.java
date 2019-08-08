package com.rahnemacollege.util;

import com.rahnemacollege.controller.AuctionController;
import com.rahnemacollege.controller.UserController;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
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

    public Resources<Resource<AuctionDomain>> toResourcesAuc(List<AuctionDomain> auctions){
       return new Resources<>(auctions.stream()
                .map(this::toResource)
                .collect(Collectors.toList()),linkTo(methodOn(AuctionController.class).all()).withSelfRel());
    }


    public Resource<AuctionDomain> toResource(AuctionDomain auction){
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(AuctionController.class).all()).withRel("all"));
        links.add( linkTo(methodOn(AuctionController.class).one(auction.getId())).withRel("self"));
        return new Resource<>(auction,links);
    }

}
