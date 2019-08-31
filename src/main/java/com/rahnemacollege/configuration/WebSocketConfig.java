package com.rahnemacollege.configuration;

import com.rahnemacollege.model.User;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.JwtTokenUtil;
import com.rahnemacollege.util.exceptions.MessageException;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.Objects;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private JwtTokenUtil tokenUtil;
    private UserService userService;
    private BidService bidService;
    private UserDetailsService userDetailsService;

    public WebSocketConfig(JwtTokenUtil tokenUtil, UserService userService, BidService bidService, UserDetailsServiceImpl userDetailsService) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.bidService = bidService;
        this.userDetailsService = userDetailsService;
    }

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(WebSocketConfig.class);



    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/auction", "/app") //socket_subscriber
                .enableSimpleBroker("/app", "/auction"); //socket_publisher
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
                    logger.info("try to connect");
                    String jwtToken = Objects.requireNonNull(headerAccessor.getFirstNativeHeader("auth")).substring(7);
                    String id = tokenUtil.getIdFromToken(jwtToken);
                    if (tokenUtil.isTokenExpired(jwtToken))
                        throw new MessageException(com.rahnemacollege.util.exceptions.Message.TOKEN_NOT_FOUND);
                    User user = userService.findUserId(Integer.valueOf(id));
                    Authentication u = new UsernamePasswordAuthenticationToken(user.getId().toString(), user.getPassword(), new ArrayList<>());
                    headerAccessor.setUser(u);
                    logger.info("the user with session id " + headerAccessor.getSessionId() + " connected");
                }
                if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
                    System.err.println(headerAccessor.getDestination().toString());
                }
                if (StompCommand.DISCONNECT.equals(headerAccessor.getCommand())) {
                    User user = userService.findUserId(Integer.valueOf(headerAccessor.getUser().getName()));
                    bidService.removeFromAllAuction(user);
                }
                return message;
            }
        });
    }

}


