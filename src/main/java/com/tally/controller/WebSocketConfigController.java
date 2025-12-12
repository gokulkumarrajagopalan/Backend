package com.tally.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class WebSocketConfigController {
    
    @GetMapping("/websocket-url")
    public Map<String, String> getWebSocketUrl() {
        Map<String, String> response = new HashMap<>();
        // Return both possible WebSocket URLs
        response.put("wsUrl", "ws://localhost:8080/ws/session");
        response.put("wsUrlWithoutContext", "ws://localhost:8080/ws/session");
        response.put("note", "Use the first URL if server has context path /api, otherwise use the second");
        return response;
    }
}
