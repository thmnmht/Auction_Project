package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuctionRepository extends CrudRepository<Auction, Integer> {

    @Query(value = " SELECT * ,COUNT(bookmarks_id) AS number_of_bookmarks " +
            "FROM Auctions left join users_bookmarks " +
            "on (Auctions.id = users_bookmarks.bookmarks_id ) " +
            "WHERE Auctions.state!=1 " +
            "group by Auctions.id " +
            "ORDER BY number_of_bookmarks DESC, Auctions.id DESC\n", nativeQuery = true)
    List<Auction> findHottest();

//    List<Auction> findByStateNotOrderByCountBy

    List<Auction> findByOwner_idOrderByIdDesc(int user_id);

    List<Auction> findByStateNotOrderByIdDesc(int states);

    List<Auction> findByStateNotAndCategory_idOrderByIdDesc(int states, int CategoryId);


}


