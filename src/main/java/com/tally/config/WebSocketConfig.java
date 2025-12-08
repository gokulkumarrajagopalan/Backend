package com.tally.config;

import com.tally.websocket.SessionWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private SessionWebSocketHandler sessionWebSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register WebSocket handler on /ws/session (becomes /api/ws/session with context path)
        registry.addHandler(sessionWebSocketHandler, "/ws/session")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()));
        
        // Also register on /session path for compatibility with frontend
        // When context path is /api, this will be /api/session
        registry.addHandler(sessionWebSocketHandler, "/session")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()));
    }
}
