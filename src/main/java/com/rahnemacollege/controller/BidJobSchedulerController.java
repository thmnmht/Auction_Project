package com.rahnemacollege.controller;

import com.rahnemacollege.job.FinalizeAuctionJob;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Bid;
import com.rahnemacollege.service.AuctionService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class BidJobSchedulerController {
    private final Logger logger = LoggerFactory.getLogger(BidJobSchedulerController.class);
    @Autowired
    private AuctionService auctionService;

    @Autowired
    private Scheduler scheduler;

    private final long auctionActiveSession = 30000L;

    @PostMapping("/schedule")
    public ResponseEntity<Auction> schedule(@RequestBody Bid bidRequest) {
        if (auctionService.findAuctionById(bidRequest.getAuction().getId()).getState() == 1)
            return ResponseEntity.badRequest().body(bidRequest.getAuction());
        try {
            JobDetail jobDetail = buildJobDetail(bidRequest);
            Trigger trigger = buildJobTrigger(jobDetail, new Date(new Date().getTime() + auctionActiveSession));
            scheduler.scheduleJob(jobDetail, trigger);
            return ResponseEntity.ok(bidRequest.getAuction());
        } catch (SchedulerException e) {
            logger.error("Error scheduling bid", e);
            return ResponseEntity.badRequest().body(bidRequest.getAuction());
        }
    }

    private JobDetail buildJobDetail(Bid bid) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("auction", bid.getAuction());
        jobDataMap.put("bidder", bid.getUser());
        return JobBuilder.newJob(FinalizeAuctionJob.class)
                .withIdentity(String.valueOf(bid.getId()), "finalizeAuction-jobs")
                .withDescription("Finalize Auction Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "finalizeAuction-triggers")
                .withDescription("Finalize Auction Trigger")
                .startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}