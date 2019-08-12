package com.rahnemacollege.repository;

import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AuctionRepository  extends PagingAndSortingRepository<Auction, Integer> {

    Page<Auction> findByTitle(String title, PageRequest pageRequest);


    //todo: consider limit for the hottest cards
    @Query(value = "SELECT * ,COUNT(bookmarks_id) AS number_of_bookmarks \n" +
            "FROM auctions\n" +
            "left join users_bookmarks\n" +
            "on (auctions.id = users_bookmarks.bookmarks_id)\n" +
            "group by\n" +
            "    auctions.id\n " +
            "ORDER BY number_of_bookmarks DESC",nativeQuery = true)
    Page<Auction> findHottest(Pageable pageable);

    Page<Auction> findByCategory(Category category, PageRequest request);
}
