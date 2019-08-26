package com.rahnemacollege.util;

import com.rahnemacollege.model.User;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserService;
import org.slf4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Arrays;

public class AuthenticationSocket extends ChannelInterceptorAdapter {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(AuthenticationSocket.class);

    private UserService userService;

    private JwtTokenUtil tokenUtil;


    public AuthenticationSocket(JwtTokenUtil tokenUtil, UserService userService) {
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
            User user = userService.findUserId(Integer.valueOf(id));
            System.out.println(id);
            Authentication u = new UsernamePasswordAuthenticationToken(user.getId().toString(), user.getPassword(), new ArrayList<>());
            headerAccessor.setUser(u);
            logger.info("the user with session id " + headerAccessor.getSessionId() + "connected");
        }
        return message;
    }
}
