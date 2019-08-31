package com.rahnemacollege.job;

import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.util.MessageHandler;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class NotifyBookmarkedAuctionJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(NotifyBookmarkedAuctionJob.class);
    private MessageHandler messageHandler;

    public NotifyBookmarkedAuctionJob(SimpMessagingTemplate template) {
        this.messageHandler = new MessageHandler(template);
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        logger.info("Executing Job with key {}", context.getJobDetail().getKey());
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        Auction auction = (Auction) jobDataMap.get("auction");
        User user = (User) jobDataMap.get("user");
        messageHandler.notify(String.valueOf(user.getId()),auction);
        logger.info("user " + user.getEmail() + " got notification for auction Id#" + auction.getId());
    }
}
