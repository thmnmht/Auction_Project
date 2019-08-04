package com.rahnemacollege.resourceAssembler;

import com.rahnemacollege.controller.AuctionController;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.resources.AuctionResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;


public class AuctionResourceAssembler extends ResourceAssemblerSupport<Auction, AuctionResource> {

    /**
     * Creates a new {@link ResourceAssemblerSupport} using the given controller class and resource type.
     *
     */
    public AuctionResourceAssembler() {
        super(AuctionController.class, AuctionResource.class);
    }

    @Override
    public AuctionResource toResource(Auction auction) {

        AuctionResource ar = super.createResourceWithId(auction.getId(),auction);
        ar.setTitle(auction.getTitle());
        ar.setDescription(auction.getDescription());
        System.err.println(ar.getLinks());
        return ar;
    }
}
