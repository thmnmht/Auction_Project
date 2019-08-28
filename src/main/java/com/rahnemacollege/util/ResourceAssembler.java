package com.rahnemacollege.util;

import com.rahnemacollege.domain.AddAuctionDomain;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceAssembler {
    public Resource<AddAuctionDomain> toResource(AddAuctionDomain auction) {
        return new Resource<>(auction);
    }
}
