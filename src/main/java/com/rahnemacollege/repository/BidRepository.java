package com.rahnemacollege.repository;

import com.rahnemacollege.model.Bid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface BidRepository extends CrudRepository<Bid, Integer> {

    @Query(value = "from Bid a join a.auction_id c where c.id=:id",nativeQuery = true)
    Iterable<Bid> findLastBid(@Param("id") String id);
}
