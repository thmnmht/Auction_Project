package com.rahnemacollege.job;

import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.EmailService;
import com.rahnemacollege.util.MessageHandler;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public class FinalizeAuctionJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(FinalizeAuctionJob.class);
    @Autowired
    private AuctionRepository repository;

    @Autowired
    private BidService bidService;
    @Autowired
    private EmailService emailService;

    @Autowired
    SimpMessagingTemplate template;

    private MessageHandler messageHandler;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Auction auction = (Auction) jobDataMap.get("auction");
        User user = (User) jobDataMap.get("bidder");
        finalizeAuction(auction, user);
    }

    private void finalizeAuction(Auction auction, User user) {
        messageHandler = new MessageHandler(template);
        if(!user.equals(auction.getOwner())){
            auction.setWinner(user);
            logger.info("User : " + user.getEmail() + " just won auction with id : " + auction.getId());
            finishAuction(auction);
            long lastPrice = bidService.findLastPrice(auction);
            messageHandler.winMessage(auction.getId(),user.getId(),auction.getTitle());
            messageHandler.ownerMessageWithWinner(auction.getOwner().getId(),auction.getId(),lastPrice,auction.getTitle());
            try {
                emailService.notifyAuctionWinner(auction, lastPrice);
                emailService.notifyAuctionOwner(auction, lastPrice);
            } catch (MessagingException e) {
                logger.error("Error while sending email, " + e.getMessage());
            }
        }
        else{
            logger.info("no one participate in auction with id " + auction.getId());
            finishAuction(auction);
            messageHandler.ownerMessage(user.getId(),auction.getId(),auction.getTitle());
        }

    }

    private void finishAuction(Auction auction){
        auction.setState(1);
        repository.save(auction);
        messageHandler.finishMessage(auction.getId());
        bidService.removeAuction(auction.getId());
    }








}