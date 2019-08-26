package com.rahnemacollege.service;


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

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    public Bid add(@NotNull BidRequest request, @NotNull User user) {
        Auction auction = auctionRepository
                .findById(request.getAuctionId()).orElseThrow(() ->
                        new InvalidInputException(Message.AUCTION_NOT_FOUND));
        if(user.equals(auction.getOwner()))
            throw new EnterDeniedException("the user is the owner of the auction");
        if (auction.getDate().getTime() >= new Date().getTime())
            throw new EnterDeniedException("the auction didn't start yet");
        if (!peopleRepository.isInAuction(auction, user))
            throw new EnterDeniedException("the user isn't in auction");
        if (bidRepository.findLatestBid(auction.getId()).isPresent()
                && bidRepository.findLatestBid(auction.getId()).get().getUser().getId().equals(user.getId())) {
            throw new InvalidInputException(Message.ALREADY_BID);
        }
        int lastPrice = findLastPrice(auction);
        if (request.getPrice() < 1)
            throw new InvalidInputException(Message.PRICE_TOO_LOW);
        Bid bid = new Bid(auction, user, lastPrice + request.getPrice(), new Date());
        bid = bidRepository.save(bid);
        return bid;
    }

    public int findLastPrice(Auction auction) {
        return bidRepository.findLatestBid(auction.getId()).map(Bid::getPrice).orElseGet(auction::getBasePrice);
    }

    public Long findLatestBidTime(Auction auction) {
        Optional<Date> date = bidRepository.findLatestBid(auction.getId()).map(Bid::getDate);
        if (date.isPresent()) {
            return date.get().getTime();
        } else {
            return null;
        }

    }

    public void enter(int auctionId, User user) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new InvalidInputException(Message.INVALID_ID));
        if (peopleRepository.isInAuction(auction, user)) {
            logger.warn("the user with id " + user.getId() + "was in auction with id " + auction.getId());
            return;
        }
        if(auction.getOwner().equals(user))
            throw new EnterDeniedException("the user is the owner of the auction");
        if(auction.getState() == 1)
            throw new EnterDeniedException("the auction was finished");
        peopleRepository.add(auction, user);
    }


    public int getMembers(Auction auction) {
        List<User> users = peopleRepository.getMembers(auction.getId());
        if (users == null)
            return 0;
        return users.size();
    }


    public void removeFromAllAuction(User user) {
        peopleRepository.ExitUser(user);
    }

    // TODO: 8/22/19 remove it
    public Map<Integer, List<User>> getUsersInAuction() {
        return peopleRepository.getUsersInAuction();
    }


    public void addSubscriptionId(String subscriptionId, int auctionId) {
        peopleRepository.addSubscriptionId(subscriptionId, auctionId);
    }

    public int getAuctionId(String subscriptionId) {
        return peopleRepository.getAuctionId(subscriptionId);
    }

}
