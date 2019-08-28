package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuctionRepository extends CrudRepository<Auction, Integer> {

    //todo: consider limit for the hottest cards
    @Query(value = "SELECT * ,COUNT(bookmarks_id) AS number_of_bookmarks \n" +
            "FROM Auctions\n" +
            "left join users_bookmarks\n" +
            "on (Auctions.id = users_bookmarks.bookmarks_id AND Auctions.state=0)\n" +
            "group by\n" +
            "Auctions.id\n " +
            "ORDER BY number_of_bookmarks DESC", nativeQuery = true)
    Page<Auction> findHottest(Pageable pageable);

    List<Auction> findByOwner_idOrderByIdDesc(int user_id);

    List<Auction> findByStateOrderByIdDesc(int state);

    List<Auction> findByStateAndCategory_idOrderByIdDesc(int state, int CategoryId);

}
