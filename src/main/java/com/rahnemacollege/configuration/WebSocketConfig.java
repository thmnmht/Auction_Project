package com.rahnemacollege.configuration;

import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.service.UserService;
import com.rahnemacollege.util.JwtTokenUtil;
import com.rahnemacollege.util.ChannelImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;



@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private BidService bidService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Autowired
    private UserService userService;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/auction");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket").setAllowedOrigins("*")
                .withSockJS();
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new ChannelImp(tokenUtil,bidService,userService));
    }

}


