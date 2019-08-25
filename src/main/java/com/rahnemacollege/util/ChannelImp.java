package com.rahnemacollege.util;

import com.rahnemacollege.model.User;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import org.slf4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;

public class ChannelImp extends ChannelInterceptorAdapter {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(ChannelImp.class);

    private BidService bidService;

    private UserService userService;

    private JwtTokenUtil tokenUtil;


    public ChannelImp(JwtTokenUtil tokenUtil, BidService bidService, UserService userService) {
        this.bidService = bidService;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            logger.info("try to connect");
            // TODO: 8/22/19 bad generate!
            String header = message.getHeaders().get("nativeHeaders").toString().replace("{", "").replace("}", "");
            String[] values = header.split(",");
            String jwtToken = Arrays.stream(values).filter(v -> v.startsWith("auth")).findFirst().get().substring(13).replace("]", "");
            System.out.println(jwtToken);
            String id = tokenUtil.getIdFromToken(jwtToken);
            User user = userService.findUserId(Integer.valueOf(id)).orElseThrow(
                    () -> new InvalidInputException(com.rahnemacollege.util.exceptions.Message.INVALID_ID)
            );
            System.out.println(id);
            Authentication u = new UsernamePasswordAuthenticationToken(user.getId().toString(), user.getPassword(), new ArrayList<>());
            headerAccessor.setUser(u);
            logger.info("the user with session id " + headerAccessor.getSessionId() + "connected");

        } else {
            Principal principal = headerAccessor.getUser();
            User user = userService.findUserId(Integer.valueOf(principal.getName())).orElseThrow(
                    () -> new InvalidInputException(com.rahnemacollege.util.exceptions.Message.INVALID_ID)
            );
            if (StompCommand.UNSUBSCRIBE.equals(headerAccessor.getCommand())) {
                bidService.removeFromAllAuction(user);
            }
        }
        return message;
    }
}
