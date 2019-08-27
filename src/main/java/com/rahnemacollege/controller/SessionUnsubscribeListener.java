package com.rahnemacollege.controller;

import com.google.gson.JsonObject;
import com.rahnemacollege.domain.Subscription;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;


@Controller
public class SessionUnsubscribeListener implements ApplicationListener<SessionUnsubscribeEvent> {


    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private BidService bidService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    private Logger logger = LoggerFactory.getLogger(SessionUnsubscribeListener.class);

    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        logger.info("someone try to exit from auction");
        GenericMessage message = (GenericMessage) event.getMessage();
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        User user = userService.findUserId(Integer.parseInt(headerAccessor.getUser().getName()));
        String subscriptionId = String.valueOf(user.getId());
        Subscription subscription = bidService.getSubscription(subscriptionId);
        bidService.removeFromAllAuction(user);
        int current = bidService.getMembers(subscription.getAuction());
        JsonObject subAlert = new JsonObject();
        subAlert.addProperty("type", 1);
        subAlert.addProperty("auctionId", subscription.getAuction().getId());
        subAlert.addProperty("current", current);
        template.convertAndSend("/app/all",subAlert.toString());
    }
}
