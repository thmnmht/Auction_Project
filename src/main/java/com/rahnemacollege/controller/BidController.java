package com.rahnemacollege.controller;


import com.rahnemacollege.domain.BidRequest;
import com.rahnemacollege.model.Bid;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.util.TokenUtil;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private UserRepository userRepository;

    @Autowired
    TokenUtil tokenUtil;

    private final Logger logger = LoggerFactory.getLogger(BidController.class);

    @MessageMapping("/bid")
    public void bid(BidRequest request, org.springframework.messaging.Message<?> message){
        logger.info("someone try to bid");
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        User user = userRepository.findById(Integer.valueOf(headerAccessor.getUser().getName())).orElseThrow(() -> new InvalidInputException(Message.INVALID_ID));
        logger.info(user.getEmail() + " with id " + user.getId() + " wants to bid auction " + request.getAuctionId());
        Bid bid = bidService.add(request,user);

        //TODO : check if auction finished

        logger.info("bid accepted");
        template.convertAndSend("/auction/" + request.getAuctionId(),bid.getPrice());
    }

}
