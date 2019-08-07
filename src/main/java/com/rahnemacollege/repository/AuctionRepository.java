package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuctionRepository  extends CrudRepository<Auction, Integer> {
}
