package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.domain.AddAuctionDomain;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.job.FakeBidJob;
import com.rahnemacollege.job.FinalizeAuctionJob;
import com.rahnemacollege.job.NotifyBookmarkedAuctionJob;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Bid;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.repository.PictureRepository;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.exceptions.Message;
import com.rahnemacollege.util.exceptions.MessageException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final PictureRepository pictureRepository;
    private final Logger logger;

    @Value("${server_ip}")
    private String ip;

    @Autowired
    private Scheduler scheduler;

    private final long auctionActiveSessionTime = 30000L;
    private final String finalizeAuctionTriggerName = "FTrigger-";
    private final String finalizeAuctionTriggerGroup = "FinalizeAuction-triggers";
    private final String finalizeAuctionJobGroup = "FinalizeAuction-jobs";

    private final long remainingTimeToNotify = 600000L;
    private final String notifyBookmarkedAuctionTriggerName = "NTrigger-";
    private final String notifyBookmarkedAuctionTriggerGroup = "NotifyAuction-triggers";
    private final String notifyBookmarkedAuctionJobGroup = "NotifyAuction-jobs";

    private final String fakeBidTriggerName = "FakeBidTrigger-";
    private final String fakeBidTriggerGroup = "FakeBid-triggers";
    private final String fakeBidJobGroup = "FakeBid-jobs";


    @Autowired
    public AuctionService(UserRepository userRepository, AuctionRepository auctionRepository, CategoryRepository categoryRepository,
                          PictureRepository pictureRepository) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.pictureRepository = pictureRepository;
        this.logger = LoggerFactory.getLogger(AuctionService.class);
    }

    public Auction addAuction(AddAuctionDomain auctionDomain, User user) {
        validation(auctionDomain);
        Auction auction = toAuction(auctionDomain, user);
        auction = auctionRepository.save(auction);
//        scheduleFakeBidOn(auction);
        return auction;
    }


    private Trigger buildFakeBidJobTrigger(JobDetail jobDetail, Date finishDate, int auctionId) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(TriggerKey.triggerKey(fakeBidTriggerName + auctionId, fakeBidTriggerGroup))
                .withDescription("Fake Bid Trigger")
                .startAt(finishDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    private JobDetail buildFakeBidJobDetail(Auction auction) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("auction", auction);
        return JobBuilder.newJob(FakeBidJob.class)
                .withIdentity(JobKey.jobKey(String.valueOf(auction.getId()), fakeBidJobGroup))
                .withDescription("Fake bid Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private void validation(AddAuctionDomain auctionDomain) {
        if (auctionDomain.getTitle() == null || auctionDomain.getTitle().length() < 1)
            throw new MessageException(Message.TITLE_NULL);
        if (auctionDomain.getTitle().length() > 50)
            throw new MessageException(Message.TITLE_TOO_LONG);
        if (auctionDomain.getDescription() != null && auctionDomain.getDescription().length() > 1000)
            throw new MessageException(Message.DESCRIPTION_TOO_LONG);
        if (auctionDomain.getDate() < 1)
            throw new MessageException(Message.DATE_NULL);
//        if (auctionDomain.getDate() - new Date().getTime() < 1800000L)
//            throw new MessageException(Message.DATE_INVALID);
        if (auctionDomain.getBasePrice() < 0)
            throw new MessageException(Message.BASE_PRICE_NULL);
        if (auctionDomain.getMaxNumber() < 2)
            throw new MessageException(Message.MAX_NUMBER_TOO_LOW);
        if (auctionDomain.getMaxNumber() > 15)
            throw new MessageException(Message.MAX_NUMBER_TOO_HIGH);
    }

    private Auction toAuction(AddAuctionDomain auctionDomain, User user) {
        Date date = new Date(auctionDomain.getDate());
        Category category = categoryRepository.findById(auctionDomain.getCategoryId()).orElseThrow(() -> new MessageException(Message.CATEGORY_INVALID));
        Auction auction = new Auction(auctionDomain.getTitle(), auctionDomain.getDescription(), auctionDomain.getBasePrice(), category, date, user, auctionDomain.getMaxNumber());
        return auction;
    }

    public Auction findAuctionById(int id) {
        return auctionRepository.findById(id).orElseThrow(() -> new MessageException(Message.AUCTION_NOT_FOUND));
    }

    public AuctionDomain toAuctionDomain(Auction auction, User user, int current) {
        AuctionDomain auctionDomain = new AuctionDomain(auction.getTitle(),
                auction.getDate().getTime(),
                auction.getCategory().getId(),
                auction.getMaxNumber(),
                auction.getId(),
                current,
                auction.getState());
        if (auction.getOwner().getId() == user.getId())
            auctionDomain.setMine(true);
        String userEmail = user.getEmail();
        user = userRepository.findByEmail(userEmail).get();
        if (user.getBookmarks().contains(auction)) {
            auctionDomain.setBookmark(true);
        }
        List<String> auctionPictures = Lists.newArrayList(pictureRepository.findAll()).stream().filter(picture ->
                picture.getFileName().contains("/" + auction.getId() + "/")).map(
                picture -> "http://" + ip + picture.getFileName()
        ).collect(Collectors.toList());
        auctionDomain.setPictures(auctionPictures);
        return auctionDomain;
    }

    public List<Category> getCategory() {
        return Lists.newArrayList(categoryRepository.findAll());
    }

    public List<Auction> getAll() {
        return new ArrayList<>(Lists.newArrayList(auctionRepository.findAll()));
    }

    public Auction findById(int id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new MessageException(Message.AUCTION_NOT_FOUND));
        return auction;
    }

    public List<Auction> findByTitle(String title, int categoryId) {
        List<Auction> auctions;
        if (categoryId == 0) {
            auctions = auctionRepository.findByStateOrderByIdDesc(0);
        } else {
            auctions = auctionRepository.findByStateAndCategory_idOrderByIdDesc(0, categoryId);
        }
        if (title != null && title.length() > 0) {
            Pattern pattern = Pattern.compile(title, Pattern.CASE_INSENSITIVE);
            auctions = auctions.stream()
                    .filter(a -> {
                        Matcher m = pattern.matcher(a.getTitle());
                        return m.find();
                    })
                    .collect(Collectors.toList());
        }
        return auctions;
    }

    public List<Auction> findByOwner(User user) {
        return auctionRepository.findByOwner_idOrderByIdDesc(user.getId());
    }

    public Page<AuctionDomain> toPage(List<AuctionDomain> list, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<AuctionDomain> pages = new PageImpl(list.subList(start, end), pageable, list.size());
        return pages;
    }

    public List<Auction> getHottest() {

        return auctionRepository.findHottest();
    }

    @Transactional
    public void addBookmark(User user, Auction auction) {
        Set<Auction> bookmarks = user.getBookmarks();
        if (bookmarks.contains(auction)) {
            bookmarks.remove(auction);
            unscheduleNotifying(user, auction);
        } else {
            bookmarks.add(auction);
            scheduleNotifying(user, auction);
        }
        userRepository.save(user);
    }

    private void unscheduleNotifying(User user, Auction bookmarkedAuction) {
        try {
            if (scheduler.checkExists(TriggerKey.triggerKey(notifyBookmarkedAuctionTriggerName + user.getId(), notifyBookmarkedAuctionTriggerGroup))
                    && scheduler.checkExists(JobKey.jobKey((bookmarkedAuction.getId() + "/" + user.getId()), notifyBookmarkedAuctionJobGroup))) {
                scheduler.unscheduleJob(TriggerKey.triggerKey(notifyBookmarkedAuctionTriggerName + user.getId(), notifyBookmarkedAuctionTriggerGroup));
                scheduler.deleteJob(JobKey.jobKey(bookmarkedAuction.getId() + "/" + user.getId(), notifyBookmarkedAuctionJobGroup));
                logger.info("auction Id#" + bookmarkedAuction.getId() + " won't be notified to user Id#" + user.getId() + " anymore. " );
            }
        } catch (SchedulerException e) {
            logger.error("Error unscheduling notification", e);
            throw new MessageException(Message.SCHEDULER_ERROR);
        }
    }

    private void scheduleNotifying(User user, Auction bookmarkedAuction) {
        int auctionId = bookmarkedAuction.getId();
        int userId = user.getId();
        try {
            Date finishDate = new Date(bookmarkedAuction.getDate().getTime() - remainingTimeToNotify);
            if (finishDate.after(new Date())) {
                JobDetail jobDetail = buildNotifyJobDetail(user, bookmarkedAuction);
                Trigger trigger = buildNotifyJobTrigger(jobDetail, finishDate, userId);
                scheduler.scheduleJob(jobDetail, trigger);
                logger.info("auction Id#" + auctionId + " will be notified to user Id#" + userId + " @ " + finishDate);
            }
        } catch (SchedulerException e) {
            logger.error("Error scheduling notification", e.toString());
            throw new MessageException(Message.SCHEDULER_ERROR);
        }

    }

    private void scheduleFakeBidOn(Auction addedAuction) {
        int auctionId = addedAuction.getId();
        try {
            Date finishDate = addedAuction.getDate();
            if (addedAuction.getState() == 0 && finishDate.after(new Date())) {
                JobDetail jobDetail = buildFakeBidJobDetail(addedAuction);
                Trigger trigger = buildFakeBidJobTrigger(jobDetail, finishDate, auctionId);
                scheduler.scheduleJob(jobDetail, trigger);
                logger.info("It will bid on auction Id#" + auctionId + " @ " + finishDate);
            }
        } catch (SchedulerException e) {
            logger.error("Error scheduling fake biding", e.toString());
            throw new MessageException(Message.SCHEDULER_ERROR);
        }
    }


    private Trigger buildNotifyJobTrigger(JobDetail jobDetail, Date finishDate, int userId) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(TriggerKey.triggerKey(notifyBookmarkedAuctionTriggerName + userId, notifyBookmarkedAuctionTriggerGroup))
                .withDescription("Notify Auction Trigger")
                .startAt(finishDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    private JobDetail buildNotifyJobDetail(User user, Auction bookmarkedAuction) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("auction", bookmarkedAuction);
        jobDataMap.put("user", user);
        return JobBuilder.newJob(NotifyBookmarkedAuctionJob.class)
                .withIdentity(JobKey.jobKey((bookmarkedAuction.getId() + "/" + user.getId()), notifyBookmarkedAuctionJobGroup))
                .withDescription("Notify Auction Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public void scheduleFinalizing(Bid bidRequest) {
        int auctionId = bidRequest.getAuction().getId();
        if (findAuctionById(auctionId).getState() == 1) {
            logger.error("cannot scheduleFinalizing, auction Id#" + auctionId + " is already finished.");
            throw new MessageException(Message.FINISHED_AUCTION);
        }
        try {
            if (scheduler.checkExists(TriggerKey.triggerKey(finalizeAuctionTriggerName + auctionId, finalizeAuctionTriggerGroup))
                    || scheduler.checkExists(JobKey.jobKey(String.valueOf(bidRequest.getId()), finalizeAuctionJobGroup))) {
                scheduler.unscheduleJob(TriggerKey.triggerKey(finalizeAuctionTriggerName + auctionId, finalizeAuctionTriggerGroup));
                scheduler.deleteJob(JobKey.jobKey(String.valueOf(bidRequest.getId()), finalizeAuctionJobGroup));
            }
        } catch (SchedulerException e) {
            logger.error("Error scheduling bid", e);
            throw new MessageException(Message.SCHEDULER_ERROR);
        }
        try {
            Date finishDate = new Date(System.currentTimeMillis() + auctionActiveSessionTime);
            JobDetail jobDetail = buildFinalizeJobDetail(bidRequest);
            Trigger trigger = buildFinalizeJobTrigger(jobDetail, finishDate, auctionId);
            scheduler.scheduleJob(jobDetail, trigger);
            logger.info("auction Id#" + auctionId + " will be finished @ " + finishDate);
        } catch (SchedulerException e) {
            logger.error("Error scheduling bid", e.toString());
            throw new MessageException(Message.SCHEDULER_ERROR);
        }
    }

    private JobDetail buildFinalizeJobDetail(Bid bid) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("auction", bid.getAuction());
        jobDataMap.put("bidder", bid.getUser());
        return JobBuilder.newJob(FinalizeAuctionJob.class)
                .withIdentity(JobKey.jobKey(String.valueOf(bid.getId()), finalizeAuctionJobGroup))
                .withDescription("Finalize Auction Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildFinalizeJobTrigger(JobDetail jobDetail, Date startAt, Integer auctionId) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(TriggerKey.triggerKey(finalizeAuctionTriggerName + auctionId, finalizeAuctionTriggerGroup))
                .withDescription("Finalize Auction Trigger")
                .startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

}
