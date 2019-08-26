package com.rahnemacollege.util;

import com.rahnemacollege.model.User;
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
import java.util.Objects;

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
            String jwtToken = Objects.requireNonNull(headerAccessor.getFirstNativeHeader("auth")).substring(7);
            String id = tokenUtil.getIdFromToken(jwtToken);
            User user = userService.findUserId(Integer.valueOf(id));
            Authentication u = new UsernamePasswordAuthenticationToken(user.getId().toString(), user.getPassword(), new ArrayList<>());
            headerAccessor.setUser(u);
            logger.info("the user with session id " + headerAccessor.getSessionId() + " connected");
        }
        return message;
    }
}
