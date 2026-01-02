package com.tally.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    @Bean
    public RateLimiter authRateLimiter(RateLimiterRegistry registry) {
        io.github.resilience4j.ratelimiter.RateLimiterConfig config = io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(10)
                .timeoutDuration(Duration.ofMillis(100))
                .build();
        
        return registry.rateLimiter("auth-limiter", config);
    }

    @Bean
    public RateLimiter loginRateLimiter(RateLimiterRegistry registry) {
        io.github.resilience4j.ratelimiter.RateLimiterConfig config = io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(15))
                .limitForPeriod(5)
                .timeoutDuration(Duration.ofMillis(100))
                .build();
        
        return registry.rateLimiter("login-limiter", config);
    }

    @Bean
    public RateLimiter apiRateLimiter(RateLimiterRegistry registry) {
        io.github.resilience4j.ratelimiter.RateLimiterConfig config = io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(100)
                .timeoutDuration(Duration.ofMillis(100))
                .build();
        
        return registry.rateLimiter("api-limiter", config);
    }
}
