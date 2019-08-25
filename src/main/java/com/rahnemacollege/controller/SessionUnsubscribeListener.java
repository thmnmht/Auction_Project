package com.rahnemacollege.controller;

import com.rahnemacollege.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Controller
public class SessionUnsubscribeListener implements ApplicationListener<SessionUnsubscribeEvent> {


    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private BidService bidService;


    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        GenericMessage message = (GenericMessage) event.getMessage();
        String subscriptionId = message.getHeaders().get("simpSubscriptionId").toString();
        System.err.println("subId in unSub " + subscriptionId);
        int auctionId = bidService.getAuctionId(subscriptionId);
        template.convertAndSend("/app", auctionId);
    }
}
