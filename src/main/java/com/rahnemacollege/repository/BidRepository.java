package com.rahnemacollege.repository;

import com.rahnemacollege.model.Bid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BidRepository extends CrudRepository<Bid, Integer> {

    //    @Query(value = "SELECT id,auction_id,user_id,price,MAX(date) FROM Bids WHERE auction_id=?1",nativeQuery = true)
//    @Query(value = "SELECT t1.* FROM Bids t1 WHERE t1.date = (SELECT MAX(t2.date) FROM Bids t2 " +
//            "WHERE t2.auction_id = ?1)", nativeQuery = true)
//    Optional<Bid> findLatestBid(Integer auctionId);

    Optional<Bid> findTopByAuction_idOrderByIdDesc(Integer auctionId);
}
