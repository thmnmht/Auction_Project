package com.rahnemacollege.repository;

import com.rahnemacollege.domain.Subscription;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.util.exceptions.EnterDeniedException;

import java.util.*;


public class OnlinePeopleRepository {

    private static OnlinePeopleRepository instance = new OnlinePeopleRepository();
    private Map<Integer, List<User>> usersInAuction;
    private Map<String, Subscription> subscriptionIds;

    private OnlinePeopleRepository() {
        usersInAuction = new HashMap<>();
        subscriptionIds = new HashMap<>();
    }

    public static OnlinePeopleRepository getInstance(){
        return instance;
    }

    public Map<Integer, List<User>> getUsersInAuction() {
        return usersInAuction;
    }

    public List<User> getMembers(int auctionId) {
        return usersInAuction.get(auctionId);
    }

    public void add(Auction auction, User user) {
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

    public void removeAuction(int auctionId){
        usersInAuction.remove(auctionId);
    }

    public void ExitUser(User user) {
        Set<Integer> auctions = usersInAuction.keySet();
        auctions.forEach(a -> usersInAuction.get(a).remove(user));
    }

    public void addSubscriptionId(String subscriptionId, Subscription subscription) {
        subscriptionIds.put(subscriptionId, subscription);
    }

    public Subscription getAuctionId(String subscriptionId) {
        return subscriptionIds.get(subscriptionId);
    }
}
