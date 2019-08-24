package com.rahnemacollege.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.util.List;

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

    private int members;
    private int lastPrice;

    public AuctionDetail(AuctionDomain auctionDomain,int members,int lastPrice) {
        this.title = auctionDomain.getTitle();
        this.description = auctionDomain.getDescription();
        this.basePrice = auctionDomain.getBasePrice();
        this.date = auctionDomain.getDate();
        this.categoryId = auctionDomain.getCategoryId();
        this.maxNumber = auctionDomain.getMaxNumber();
        this.pictures = auctionDomain.getPictures();
        this.mine = auctionDomain.isMine();
        this.bookmark = auctionDomain.isBookmark();
        this.id = auctionDomain.getId();
        this.members = members;
        this.lastPrice = lastPrice;
    }


}
