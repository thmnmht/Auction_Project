package com.rahnemacollege.controller;


import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.google.gson.Gson;
import com.rahnemacollege.domain.BidRequest;
import com.rahnemacollege.model.Bid;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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

    // TODO: 8/21/19 remove user repository
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
        User user = userService.findUserId(Integer.valueOf(headerAccessor.getUser().getName())).orElseThrow(() -> new InvalidInputException(com.rahnemacollege.util.exceptions.Message.TOKEN_NOT_FOUND));
        logger.info(user.getEmail() + " with id " + user.getId() + " wants to bid auction " + request.getAuctionId());
        Bid bid = bidService.add(request, user);
        auctionService.schedule(bid);
        logger.info("bid accepted");
        template.convertAndSend("/auction/" + request.getAuctionId(), bid.getPrice());
    }

    @SubscribeMapping("/{auctionId}")
    public void getViewSchema(@DestinationVariable("auctionId") int auctionId,
                              Message<?> message) throws Exception {
        logger.info("someone try to enter auction " + auctionId);
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        bidService.addSubscriptionId(headerAccessor.getSubscriptionId(), auctionId);
        User user = userService.findUserId(Integer.valueOf(headerAccessor.getUser().getName())).orElseThrow(
                () -> new InvalidInputException(com.rahnemacollege.util.exceptions.Message.INVALID_ID));
        bidService.removeFromAllAuction(user);
        bidService.enter(auctionId, user);
        bidService.getUsersInAuction().keySet().forEach(a -> bidService.getUsersInAuction().get(a).forEach(u ->
                logger.info("user " + u.getId() + " is in auction " + a)));
        template.convertAndSend("/app", auctionId);
    }


}