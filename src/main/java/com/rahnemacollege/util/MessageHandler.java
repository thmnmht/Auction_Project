package com.rahnemacollege.util;

import com.google.gson.JsonObject;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.util.exceptions.MessageException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class MessageHandler {

    private SimpMessagingTemplate template;

    public MessageHandler(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void winMessage(int auctionId, int userId, String title) {
        JsonObject winnerAlert = new JsonObject();
        winnerAlert.addProperty("type", 3);
        winnerAlert.addProperty("auctionId", auctionId);
        winnerAlert.addProperty("title", title);
        template.convertAndSendToUser(String.valueOf(userId), "/app/all", winnerAlert.toString());
    }

    public void finishMessage(int auctionId) {
        JsonObject finishAlert = new JsonObject();
        finishAlert.addProperty("type", 4);
        finishAlert.addProperty("auctionId", auctionId);
        template.convertAndSend("/app/all", finishAlert.toString());
    }

    public void ownerMessageWithWinner(int userId, int auctionId, long lastPrice, String title) {
        JsonObject alert = new JsonObject();
        alert.addProperty("type", 6);
        alert.addProperty("auctionId", auctionId);
        alert.addProperty("price", lastPrice);
        alert.addProperty("title", title);
        template.convertAndSendToUser(String.valueOf(userId), "/app/all", alert.toString());
    }

    public void ownerMessage(int userId,int auctionId,String title){
        JsonObject alert = new JsonObject();
        alert.addProperty("type", 8);
        alert.addProperty("auctionId", auctionId);
        alert.addProperty("title", title);
        template.convertAndSendToUser(String.valueOf(userId), "/app/all", alert.toString());
    }

    public void exceptionMessage(MessageException exception, String userId) {
        JsonObject errAlert = new JsonObject();
        int type = getExceptionType(exception.getMessageStatus());
        errAlert.addProperty("type", type);
        errAlert.addProperty("message", exception.getMessage());
        errAlert.addProperty("code", exception.getMessageStatus().ordinal());
        template.convertAndSendToUser(userId, "/app/all", errAlert.toString());

    }

    public void notify(String userId, Auction auction) {
        JsonObject alert = new JsonObject();
        alert.addProperty("type", 9);
        alert.addProperty("auctionId", auction.getId());
        alert.addProperty("title", auction.getTitle());
        template.convertAndSendToUser(String.valueOf(userId), "/app/all", alert.toString());
    }

    public void subscribeMessage(int auctionId, int current) {
        JsonObject subAlert = new JsonObject();
        subAlert.addProperty("type", 1);
        subAlert.addProperty("auctionId", auctionId);
        subAlert.addProperty("current", current);
        template.convertAndSend("/app/all", subAlert.toString());

    }

    public void newBidMessage(int auctionId, long bidPrice, boolean fake) {
        JsonObject bidAlert = new JsonObject();
        bidAlert.addProperty("price", bidPrice);
        bidAlert.addProperty("fake", fake);
        template.convertAndSend("/auction/id/" + auctionId, bidAlert.toString());
    }

    public void myBidMessage(int userId, long price) {
        JsonObject myBidAlert = new JsonObject();
        myBidAlert.addProperty("type", 5);
        myBidAlert.addProperty("price", price);
        myBidAlert.addProperty("mine", true);
        template.convertAndSendToUser(String.valueOf(userId), "/app/all", myBidAlert.toString());

    }

    private int getExceptionType(com.rahnemacollege.util.exceptions.Message message) {
        if (message.equals(com.rahnemacollege.util.exceptions.Message.FINISHED_AUCTION))
            return 7;
        if (message.equals(com.rahnemacollege.util.exceptions.Message.AUCTION_IS_FULL))
            return 7;
        if (message.equals(com.rahnemacollege.util.exceptions.Message.THE_AUCTION_DIDNT_START_YET))
            return 7;
        return 2;
    }

}
