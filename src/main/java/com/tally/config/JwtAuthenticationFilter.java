package com.tally.config;

import com.tally.entity.User;
import com.tally.service.UserService;
import com.tally.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    @org.springframework.context.annotation.Lazy
    private UserService userService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip WebSocket upgrade requests
        String upgrade = request.getHeader("Upgrade");
        if (upgrade != null && upgrade.equalsIgnoreCase("websocket")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip JWT authentication for sync endpoints
        String requestPath = request.getRequestURI();
        if (isSyncEndpoint(requestPath) || isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                logger.debug("Token extracted from Authorization header");
                username = jwtUtil.extractUsername(token);
            }
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("User authentication processing");
                if (jwtUtil.isTokenValid(token)) {
                    // Validate device token for single device login
                    String deviceToken = request.getHeader("X-Device-Token");
                    Optional<User> user = userService.findByUsername(username);
                    
                    if (user.isPresent()) {
                        String storedDeviceToken = user.get().getDeviceToken();
                        
                        if (deviceToken != null && deviceToken.equals(storedDeviceToken)) {
                            UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("Authentication successful");
                        } else {
                            logger.warn("Device token validation failed");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Device token invalid. User may be logged in from another device.\"}");
                            response.setContentType("application/json");
                            return;
                        }
                    }
                } else {
                    logger.warn("Token validation failed");
                }
            } else if (username != null) {
                logger.debug("Authentication already set, skipping");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isSyncEndpoint(String path) {
        return path.endsWith("/sync") || 
               path.matches(".*/sync$") ||
               path.contains("/sync/");
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth/") ||
               path.startsWith("/ws/") ||
               path.equals("/session") ||
               path.startsWith("/config/");
    }
}
