package com.tally.service.impl;

import com.tally.service.interfaces.IResponseBuilder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Response Builder Service Implementation
 * Responsible for standardizing API responses
 * Follows Single Responsibility Principle (SRP)
 */
@Service
public class ResponseBuilder implements IResponseBuilder {
    
    @Override
    public Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @Override
    public Map<String, Object> success(String message) {
        return success(message, null);
    }
    
    @Override
    public Map<String, Object> error(String message) {
        return error(message, 500);
    }
    
    @Override
    public Map<String, Object> error(String message, int statusCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("statusCode", statusCode);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @Override
    public Map<String, Object> paginated(String message, Object data, int page, int pageSize, long total) {
        Map<String, Object> response = success(message, data);
        response.put("pagination", new HashMap<String, Object>() {{
            put("page", page);
            put("pageSize", pageSize);
            put("total", total);
            put("totalPages", (total + pageSize - 1) / pageSize);
        }});
        return response;
    }
}
