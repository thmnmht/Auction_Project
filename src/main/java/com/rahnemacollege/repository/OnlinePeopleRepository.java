package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.util.exceptions.EnterDeniedException;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class OnlinePeopleRepository {

    private Map<Integer, List<User>> onlinePeople = new HashMap<>();


    public Map<Integer, List<User>> getOnlinePeople() {
        return onlinePeople;
    }

    public List<User> getMembers(int auctionId){
        return onlinePeople.get(auctionId);
    }

    public void add(Auction auction, User user){
        System.err.println("maxN:"+auction.getMaxNumber());
        if (onlinePeople.containsKey(auction.getId())) {
            int maxNumber = auction.getMaxNumber();
            int onlineNumber = onlinePeople.get(auction.getId()).size();
            if (maxNumber <= onlineNumber) {
//                logger.error("the auction with id " + auction.getId() + "is full");
                throw new EnterDeniedException();
            }
            onlinePeople.get(auction.getId()).add(user);
        } else {
            List<User> tmp = new ArrayList<>();
            tmp.add(user);
            onlinePeople.putIfAbsent(auction.getId(), tmp);
        }
    }

    public boolean isInAuction(Auction auction, User user) {
        if (onlinePeople.keySet().contains(auction.getId())) {
            if (onlinePeople.get(auction.getId()).contains(user))
                return true;
        }
        return false;
    }

    public void ExitUser(User user){
        Set<Integer> auctions = onlinePeople.keySet();
        auctions.forEach(a -> onlinePeople.get(a).remove(user));
    }
}
