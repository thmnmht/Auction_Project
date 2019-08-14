package com.rahnemacollege.util;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.domain.UserDomain;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResourceAssembler {
    public Resource<UserDomain> toResource(UserDomain user) {
        return null;
    }

    public Resources<Resource<UserDomain>> toResourcesUser(List<UserDomain> users) {
        return new Resources<>(users.stream()
                .map(this::toResource)
                .collect(Collectors.toList()));
    }

    public Resources<Resource<AuctionDomain>> toResourcesAuc(List<AuctionDomain> auctions) {
        return new Resources<>(auctions.stream()
                .map(this::toResource)
                .collect(Collectors.toList()));
    }


    public Resource<AuctionDomain> toResource(AuctionDomain auction) {
        List<Link> links = new ArrayList<>();
        return new Resource<>(auction, links);
    }


}
