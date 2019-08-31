package com.rahnemacollege.util;

import com.rahnemacollege.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitSetup {
    @Autowired
    private AuctionService auctionService;

    public void run() {
        auctionService.initialReschedule();
    }
}
