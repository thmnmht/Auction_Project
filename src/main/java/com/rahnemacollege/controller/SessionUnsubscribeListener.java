package com.rahnemacollege.controller;

import com.google.gson.JsonObject;
import com.rahnemacollege.domain.Subscription;
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
        Subscription subscription = bidService.getSubscription(subscriptionId);
        bidService.removeFromAllAuction(subscription.getUser());
        int current = bidService.getMembers(subscription.getAuction());
        JsonObject subAlert = new JsonObject();
        subAlert.addProperty("type", 1);
        subAlert.addProperty("auctionId", subscription.getAuction().getId());
        subAlert.addProperty("current", current);
        template.convertAndSend("/app/all",subAlert.toString());
    }
}
