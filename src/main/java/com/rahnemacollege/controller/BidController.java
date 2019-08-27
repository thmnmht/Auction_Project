package com.rahnemacollege.controller;


import com.google.gson.JsonObject;
import com.rahnemacollege.domain.BidRequest;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Bid;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class BidController {


    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private BidService bidService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    TokenUtil tokenUtil;

    private final Logger logger = LoggerFactory.getLogger(BidController.class);

    @MessageMapping("/bid")
    public void bid(BidRequest request, Message<?> message) {
        logger.info("someone try to bid");
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        User user = userService.findUserId(Integer.valueOf(headerAccessor.getUser().getName()));
        logger.info(user.getEmail() + " with id " + user.getId() + " wants to bid auction " + request.getAuctionId());
        Bid bid = bidService.add(request, user);
//        auctionService.schedule(bid);
        JsonObject bidAlert = new JsonObject();
        bidAlert.addProperty("price", bid.getPrice());
        logger.info("bid accepted");
        template.convertAndSend("/auction/id/" + request.getAuctionId(), bidAlert.toString());
        JsonObject myBidAlert = new JsonObject();
        myBidAlert.addProperty("type", 5);
        myBidAlert.addProperty("price", bid.getPrice());
        myBidAlert.addProperty("mine", true);
        template.convertAndSendToUser(String.valueOf(user.getId()),"/app/all", myBidAlert.toString());
    }

    @SubscribeMapping("/id/{auctionId}")
    public void enterAuction(@DestinationVariable("auctionId") int auctionId,
                              Message<?> message) throws Exception {
        logger.info("someone try to enter auction " + auctionId);
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Auction auction = auctionService.findById(auctionId);
        User user = userService.findUserId(Integer.valueOf(headerAccessor.getUser().getName()));
        bidService.removeFromAllAuction(user);
        bidService.addSubscriptionId(String.valueOf(user.getId()), auction, user);
        int current = bidService.enter(auction, user);
        JsonObject subAlert = new JsonObject();
        subAlert.addProperty("type", 1);
        subAlert.addProperty("auctionId", auctionId);
        subAlert.addProperty("current", current);
        template.convertAndSend("/app/all",subAlert.toString());
    }


    @MessageExceptionHandler
    public void enterDenied(MessageException e, Message<?> message) throws Exception{
        logger.error("the exception is : " + e.getMessage());
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String user = headerAccessor.getUser().getName();
        JsonObject errAlert = new JsonObject();
        errAlert.addProperty("type",2);
        errAlert.addProperty("message",e.getMessage());
        errAlert.addProperty("code",e.getMessageStatus().ordinal());
        template.convertAndSendToUser(user,"/app/all",errAlert.toString());

    }


}
