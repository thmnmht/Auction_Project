package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuctionRepository extends CrudRepository<Auction, Integer> {

    @Query(value = " SELECT * ,COUNT(bookmarks_id) AS number_of_bookmarks " +
            "FROM Auctions left join users_bookmarks " +
            "on (Auctions.id = users_bookmarks.bookmarks_id ) " +
            "WHERE Auctions.state!=1 " +
            "group by Auctions.id " +
            "ORDER BY number_of_bookmarks DESC, Auctions.id DESC\n",
            countQuery = "SELECT count(*) FROM Auctions",
            nativeQuery = true)
    Page<Auction> findHottest(Pageable pageable);

    Page<Auction> findByOwner_idOrderByIdDesc(int userId, Pageable pageable);

    Page<Auction> findByStateNotOrderByIdDesc(int state, Pageable pageable);

    Page<Auction> findByStateNotAndCategory_idOrderByIdDesc(int state, int CategoryId, Pageable pageable);


}


