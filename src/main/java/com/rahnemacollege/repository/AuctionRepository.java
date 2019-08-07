package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuctionRepository  extends CrudRepository<Auction, Integer> {

    @Override
    Optional<Auction> findById(Integer integer);

    Optional<Auction> findByTitle(String title);

}
