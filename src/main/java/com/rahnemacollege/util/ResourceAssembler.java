package com.rahnemacollege.util;

import com.rahnemacollege.controller.AuctionController;
import com.rahnemacollege.controller.UserController;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.domain.SimpleUserDomain;
import com.rahnemacollege.domain.UserDomain;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ResourceAssembler {

    public Resource<UserDomain> toResource(UserDomain user, HttpServletRequest request) {
        return new Resource<>(user,
                linkTo(methodOn(UserController.class).one(request)).withSelfRel());
    }


    public Resources<Resource<UserDomain>> toResourcesUser(List<UserDomain> users,HttpServletRequest request) {
        return new Resources<>(users.stream()
                .map(u -> toResource(u,request))
                .collect(Collectors.toList()));
    }

    public Resources<Resource<AuctionDomain>> toResourcesAuc(List<AuctionDomain> auctions,HttpServletRequest request) {
        return new Resources<>(auctions.stream()
                .map(a -> toResource(a,request))
                .collect(Collectors.toList()));
    }


    public Resource<AuctionDomain> toResource(AuctionDomain auction,HttpServletRequest request) {
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(AuctionController.class).all(request)).withRel("all"));
        links.add(linkTo(methodOn(AuctionController.class).one(auction.getId(),request)).withRel("self"));
        return new Resource<>(auction, links);
    }

}
