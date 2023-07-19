package com.bradesco.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtDecoder jwtDecoder;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic").setTaskScheduler(heartBeatScheduler());;
        config.setApplicationDestinationPrefixes("/current");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    List<String> authorization = accessor.getNativeHeader("X-Authorization");
                    logger.info("X-Authorization: {}", authorization);

                    String accessToken = authorization.get(0).split(" ")[1];
                    Jwt jwt = jwtDecoder.decode(accessToken);

                    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                    converter.setPrincipalClaimName("preferred_username");
                    Authentication authentication = converter.convert(jwt);
                    accessor.setUser(authentication);
                }
                return message;
            }
        });

    }
    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
