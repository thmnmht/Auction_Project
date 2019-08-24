package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.domain.BidRequest;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Bid;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.BidRepository;
import com.rahnemacollege.repository.OnlinePeopleRepository;
import com.rahnemacollege.util.exceptions.EnterDeniedException;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BidService {


    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private OnlinePeopleRepository peopleRepository;


    private Logger logger;


    public BidService() {
        logger = LoggerFactory.getLogger(BidService.class);
    }


    public Bid add(BidRequest request, User user) {
        Auction auction = auctionRepository
                .findById(request.getAuctionId()).orElseThrow(() ->
                        new InvalidInputException(Message.AUCTION_NOT_FOUND));
        if (!peopleRepository.isInAuction(auction, user))
            throw new EnterDeniedException();
        int lastPrice = findLastPrice(auction);
        if (request.getPrice() < 1)
            throw new InvalidInputException(Message.PRICE_TOO_LOW);
        Bid bid = new Bid(auction, user, lastPrice + request.getPrice(), new Date());
        bid = bidRepository.save(bid);
        return bid;
    }

    public int findLastPrice(Auction auction) {
        // TODO: 8/21/19 get last bid with query
//        List<Bid> lastBid = Lists.newArrayList(bidRepository.findLastBid(String.valueOf(auction.getId())));
        List<Bid> lastBid = Lists.newArrayList(bidRepository.findAll()).stream().filter(b -> b.getAuction().equals(auction)).collect(Collectors.toList());

        int lastPrice = auction.getBasePrice();
        if (lastBid.size() > 0)
            lastPrice = lastBid.get(lastBid.size() - 1).getPrice();
        return lastPrice;
    }

    public void enter(int auctionId, User user) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new InvalidInputException(Message.INVALID_ID));
        if (peopleRepository.isInAuction(auction, user)) {
            logger.warn("the user with id " + user.getId() + "was in auction with id " + auction.getId());
            return;
        }
        peopleRepository.add(auction,user);
    }


    public int getMembers(Auction auction){
        List<User> users = peopleRepository.getMembers(auction.getId());
        if(users == null)
            return 0;
        return users.size();
    }


    public void removeFromAllAuction(User user) {
        peopleRepository.ExitUser(user);
    }

    // TODO: 8/22/19 remove it
    public Map<Integer, List<User>> getOnlinePeople() {
        return peopleRepository.getOnlinePeople();
    }
}
