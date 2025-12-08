package com.tally.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tally.entity.User;
import com.tally.service.UserService;
import com.tally.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionWebSocketHandler extends TextWebSocketHandler {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Map: userId -> WebSocketSession
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("\n🔌 WebSocket connection established: " + session.getId());
        System.out.println("   Remote address: " + session.getRemoteAddress());
        
        // Extract token and deviceToken from query parameters
        String query = session.getUri().getQuery();
        Map<String, String> params = parseQueryString(query);
        
        String token = params.get("token");
        String deviceToken = params.get("deviceToken");
        
        if (token == null || token.isEmpty()) {
            System.err.println("❌ Missing or empty token parameter");
            sendMessage(session, Map.of(
                "type", "ERROR",
                "message", "Missing authentication token"
            ));
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        
        if (deviceToken == null || deviceToken.isEmpty()) {
            System.err.println("❌ Missing or empty deviceToken parameter");
            sendMessage(session, Map.of(
                "type", "ERROR",
                "message", "Missing device token"
            ));
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        
        try {
            // Validate JWT token
            if (!jwtUtil.isTokenValid(token)) {
                System.err.println("❌ Invalid JWT token");
                sendMessage(session, Map.of(
                    "type", "ERROR",
                    "message", "Invalid token"
                ));
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            
            // Get username from token
            String username = jwtUtil.extractUsername(token);
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                System.err.println("❌ User not found: " + username);
                sendMessage(session, Map.of(
                    "type", "ERROR",
                    "message", "User not found"
                ));
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            
            User user = userOpt.get();
            Long userId = user.getId();
            
            // Validate device token
            String storedDeviceToken = user.getDeviceToken();
            if (storedDeviceToken == null || !deviceToken.equals(storedDeviceToken)) {
                System.err.println("❌ Device token mismatch for user: " + userId + " (" + username + ")");
                System.err.println("   Expected: " + storedDeviceToken);
                System.err.println("   Received: " + deviceToken);
                sendMessage(session, Map.of(
                    "type", "ERROR",
                    "message", "Device token mismatch"
                ));
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            
            // Check if user already has an active session
            if (userSessions.containsKey(userId)) {
                WebSocketSession oldSession = userSessions.get(userId);
                
                System.out.println("⚠️ User " + userId + " (" + username + ") logged in from new device");
                System.out.println("   Old session ID: " + oldSession.getId());
                System.out.println("   New session ID: " + session.getId());
                System.out.println("   Action: Kicking out old session");
                
                // Send logout message to old session
                sendMessage(oldSession, Map.of(
                    "type", "SESSION_INVALIDATED",
                    "reason", "You have been logged in from another device",
                    "timestamp", System.currentTimeMillis()
                ));
                
                try {
                    oldSession.close(CloseStatus.NORMAL);
                } catch (Exception e) {
                    System.err.println("   ⚠️ Error closing old session: " + e.getMessage());
                }
            }
            
            // Register new session
            userSessions.put(userId, session);
            session.getAttributes().put("userId", userId);
            session.getAttributes().put("username", username);
            
            System.out.println("✅ WebSocket session registered for user: " + userId + " (" + username + ")");
            System.out.println("   Session ID: " + session.getId());
            System.out.println("   Active sessions: " + userSessions.size());
            System.out.println("");
            
            // Send connection success message
            sendMessage(session, Map.of(
                "type", "CONNECTED",
                "message", "Session monitoring active",
                "userId", userId,
                "username", username
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Error establishing WebSocket connection: " + e.getMessage());
            e.printStackTrace();
            try {
                sendMessage(session, Map.of(
                    "type", "ERROR",
                    "message", e.getMessage()
                ));
            } catch (Exception e2) {
                System.err.println("❌ Error sending error message: " + e2.getMessage());
            }
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("📨 Received message from " + session.getId() + ": " + payload);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            String type = (String) data.get("type");
            
            if ("HEARTBEAT".equals(type)) {
                // Respond to heartbeat
                Long userId = (Long) session.getAttributes().get("userId");
                System.out.println("   💓 HEARTBEAT from user " + userId);
                sendMessage(session, Map.of(
                    "type", "HEARTBEAT_ACK",
                    "timestamp", System.currentTimeMillis()
                ));
            } else if ("PING".equals(type)) {
                // Simple ping-pong
                System.out.println("   🏓 PING from " + session.getId());
                sendMessage(session, Map.of("type", "PONG"));
            } else {
                System.out.println("   ℹ️ Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.err.println("❌ Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        String username = (String) session.getAttributes().get("username");
        
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("🔌 WebSocket disconnected for user: " + userId + " (" + username + ")");
            System.out.println("   Reason: " + status);
            System.out.println("   Active sessions: " + userSessions.size());
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("❌ WebSocket transport error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }
    
    /**
     * Send session invalidation message to client
     */
    private void sendInvalidSessionMessage(WebSocketSession session, String reason) {
        try {
            if (session.isOpen()) {
                sendMessage(session, Map.of(
                    "type", "SESSION_INVALIDATED",
                    "reason", reason,
                    "timestamp", System.currentTimeMillis()
                ));
            }
        } catch (Exception e) {
            System.err.println("❌ Error sending invalidation message: " + e.getMessage());
        }
    }
    
    /**
     * Send message to WebSocket client
     */
    private void sendMessage(WebSocketSession session, Map<String, Object> data) throws IOException {
        if (session.isOpen()) {
            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        }
    }
    
    /**
     * Invalidate session for a specific user (call this when user logs in from new device)
     */
    public void invalidateUserSession(Long userId, String reason) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                System.out.println("\n🚫 Invalidating session for user: " + userId);
                System.out.println("   Reason: " + reason);
                System.out.println("   Session ID: " + session.getId());
                
                sendMessage(session, Map.of(
                    "type", "SESSION_INVALIDATED",
                    "reason", reason,
                    "timestamp", System.currentTimeMillis()
                ));
                session.close(CloseStatus.NORMAL);
                userSessions.remove(userId);
                
                System.out.println("✅ Session invalidated successfully");
                System.out.println("");
            } catch (Exception e) {
                System.err.println("❌ Error invalidating session: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ℹ️ No active session found for user: " + userId);
        }
    }
    
    /**
     * Parse query string into map with URL decoding
     */
    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                        params.put(key, value);
                    } catch (Exception e) {
                        System.err.println("Error decoding parameter: " + pair);
                    }
                }
            }
        }
        return params;
    }
    
    /**
     * Get count of active sessions
     */
    public int getActiveSessionCount() {
        return userSessions.size();
    }
    
    /**
     * Check if user has active session
     */
    public boolean hasActiveSession(Long userId) {
        return userSessions.containsKey(userId);
    }
}
