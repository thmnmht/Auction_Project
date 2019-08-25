package com.rahnemacollege.domain;

import lombok.Data;

import java.util.List;

@Data
public class AuctionDetail {

    private String title;
    private String description;
    private int basePrice = -1;
    private long date = -1;
    private int categoryId;
    private int maxNumber = -1;
    private boolean bookmark = false;
    private boolean mine = false;
    private int id;
    private List<String> pictures;
    private int current;
    private int lastPrice;

    public AuctionDetail(AuctionDomain auctionDomain,
                         String description,
                         int basePrice,
                         int lastPrice) {
        this.title = auctionDomain.getTitle();
        this.description = description;
        this.basePrice = basePrice;
        this.date = auctionDomain.getDate();
        this.categoryId = auctionDomain.getCategoryId();
        this.maxNumber = auctionDomain.getMaxNumber();
        this.pictures = auctionDomain.getPictures();
        this.mine = auctionDomain.isMine();
        this.bookmark = auctionDomain.isBookmark();
        this.id = auctionDomain.getId();
        this.current = auctionDomain.getCurrent();
        this.lastPrice = lastPrice;
    }


}
