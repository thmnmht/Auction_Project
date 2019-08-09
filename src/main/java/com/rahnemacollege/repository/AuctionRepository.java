package com.rahnemacollege.repository;

import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuctionRepository extends CrudRepository<Auction, Integer> {

    @Override
    Optional<Auction> findById(Integer integer);

    Page<Auction> findByTitle(String title, PageRequest pageRequest);

    @Query("SELECT * ,COUNT(bookmarks_id) AS number_of_bookmarks \n" +
            "FROM auctions\n" +
            "left join users_bookmarks\n" +
            "on (auctions.id = users_bookmarks.bookmarks_id)\n" +
            "group by\n" +
            "    auctions.id\n " +
            "ORDER BY number_of_bookmarks DESC")
    Page<Auction> findHottest(Pageable pageable);


}
