package com.rahnemacollege.job;

import com.rahnemacollege.model.Auction;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FakeBidJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(FinalizeAuctionJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) {
        logger.info("Executing Job with key {}", context.getJobDetail().getKey());
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        Auction auction = (Auction) jobDataMap.get("auction");
        //Todo
        logger.info("Fake biding on auction Id#"+auction.getId()+" @"+new Date());
    }
}
