package com.rahnemacollege.job;

import com.rahnemacollege.controller.BidController;
import com.rahnemacollege.domain.BidRequest;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.service.BidService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FakeBidJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(FinalizeAuctionJob.class);

    @Autowired
    private BidService bidService;

    @Autowired
    private BidController bidController;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        logger.info("Executing Job with key {}", context.getJobDetail().getKey());
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        Auction auction = (Auction) jobDataMap.get("auction");
        BidRequest bidRequest = new BidRequest();
        bidRequest.setPrice(0L);
        bidRequest.setAuctionId(auction.getId());
        //Todo

//        bidService.add(bidRequest,auction.getOwner());
//        bidController.bid(bidRequest,?);
        logger.info("Fake biding on auction Id#"+auction.getId()+" @"+new Date());
    }
}
