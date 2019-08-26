package com.rahnemacollege.domain;


import lombok.Data;

@Data
public class SubscribeAlert {

    private int auctionId;
    private int current;

    public SubscribeAlert(int auctionId,int current){
        this.auctionId = auctionId;
        this.current = current;
    }

}
