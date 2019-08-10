package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuctionRepository  extends PagingAndSortingRepository<Auction, Integer> {

//    @Override
//    Optional<Auction> findById(Integer integer);

    Optional<Auction> findByTitle(String title);

//    Page<Auction> findAll(Pageable firstPageWithTwoElements);
}
