package com.rahnemacollege.job;

import com.google.gson.JsonObject;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.service.BidService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class FinalizeAuctionJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(FinalizeAuctionJob.class);
    @Autowired
    private AuctionRepository repository;


    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private BidService bidService;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Auction auction = (Auction) jobDataMap.get("auction");
        User user = (User) jobDataMap.get("bidder");
        finalizeAuction(auction, user);
    }

    private void finalizeAuction(Auction auction, User user) {
        setWinner(auction,user);
        finishAuction(auction);
        ownerAlert(auction);
    }

    private void setWinner(Auction auction,User user){
        auction.setWinner(user);
        auction.setState(1);
        repository.save(auction);
        logger.info("User : " + user.getEmail() + " just won auction with id : " + auction.getId());
        JsonObject winnerAlert = new JsonObject();
        winnerAlert.addProperty("type",3);
        winnerAlert.addProperty("auctionId",auction.getId());
        template.convertAndSendToUser(String.valueOf(user.getId()),"/app/all", winnerAlert.toString());
    }

    private void finishAuction(Auction auction){
        JsonObject finishAlert = new JsonObject();
        finishAlert.addProperty("type",4);
        finishAlert.addProperty("auctionId",auction.getId());
        template.convertAndSend("/app/all", finishAlert.toString());
        bidService.removeAuction(auction.getId());
    }

    private void ownerAlert(Auction auction){
        JsonObject alert = new JsonObject();
        alert.addProperty("type",6);
        alert.addProperty("auctionId",auction.getId());
        template.convertAndSendToUser(String.valueOf(auction.getOwner().getId()),"/app/all", alert.toString());
    }




}