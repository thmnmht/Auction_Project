package com.rahnemacollege.util;

import com.rahnemacollege.controller.AuctionController;
import com.rahnemacollege.controller.UserController;
import com.rahnemacollege.domain.UserDomain;
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

    public Resource<UserDomain> toResource(UserDomain userDomain){
        return new Resource<>(userDomain, (linkTo(methodOn(UserController.class).all()).withRel("all")));
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
        List<Link> links = new ArrayList<>();
//        File dir = new File("./src/main/resources/image/" + auction.getId() + "/");
//        File[] directoryListing = dir.listFiles();
//        if (directoryListing != null) {
//            int counter = 1;
//            for (File child : directoryListing) {
//                links.add(linkTo("???" + child.getName()).withRel("image " + counter));
//                counter++;
//            }
//        }
        links.add(linkTo(methodOn(AuctionController.class).all()).withRel("all"));
        links.add( linkTo(methodOn(AuctionController.class).one(auction.getId())).withRel("self"));
        return new Resource<>(auction,links);
    }

}
