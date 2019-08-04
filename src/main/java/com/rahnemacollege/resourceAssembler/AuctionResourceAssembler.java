package com.rahnemacollege.resourceAssembler;

import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.resources.AuctionResource;
import com.rahnemacollege.resources.UserResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;


public class AuctionResourceAssembler extends ResourceAssemblerSupport<Auction, AuctionResource> {

    /**
     * Creates a new {@link ResourceAssemblerSupport} using the given controller class and resource type.
     *
     */
    public AuctionResourceAssembler() {
        super(Auction.class, AuctionResource.class);
    }

    @Override
    public AuctionResource toResource(Auction auction) {

        AuctionResource ar = super.createResourceWithId(auction.getId(),auction);
        ar.setTitle(auction.getTitle());
        ar.setDescription(auction.getDescription());
        return ar;
    }
}
