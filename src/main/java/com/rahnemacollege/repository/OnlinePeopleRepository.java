package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.util.exceptions.EnterDeniedException;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class OnlinePeopleRepository {

    private Map<Integer, List<User>> usersInAuction;
    private Map<String, Integer> subscriptionIds;

    public OnlinePeopleRepository() {
        usersInAuction = new HashMap<>();
        subscriptionIds = new HashMap<>();
    }

    public Map<Integer, List<User>> getUsersInAuction() {
        return usersInAuction;
    }

    public List<User> getMembers(int auctionId) {
        return usersInAuction.get(auctionId);
    }

    public void add(Auction auction, User user) {
        System.err.println("maxN:" + auction.getMaxNumber());
        if (usersInAuction.containsKey(auction.getId())) {
            int maxNumber = auction.getMaxNumber();
            int onlineNumber = usersInAuction.get(auction.getId()).size();
            if (maxNumber <= onlineNumber) {
                throw new EnterDeniedException("the auction is full");
            }
            usersInAuction.get(auction.getId()).add(user);
        } else {
            List<User> tmp = new ArrayList<>();
            tmp.add(user);
            usersInAuction.putIfAbsent(auction.getId(), tmp);
        }
    }

    public boolean isInAuction(Auction auction, User user) {
        if (usersInAuction.keySet().contains(auction.getId())) {
            if (usersInAuction.get(auction.getId()).contains(user))
                return true;
        }
        return false;
    }

    public void ExitUser(User user) {
        Set<Integer> auctions = usersInAuction.keySet();
        auctions.forEach(a -> usersInAuction.get(a).remove(user));
    }

    public void addSubscriptionId(String subscriptionId, int auctionId) {
        subscriptionIds.put(subscriptionId, auctionId);
    }

    public int getAuctionId(String subscriptionId) {
        return subscriptionIds.get(subscriptionId);
    }
}
