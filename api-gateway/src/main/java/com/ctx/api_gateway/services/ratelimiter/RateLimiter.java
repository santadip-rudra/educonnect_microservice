package com.ctx.api_gateway.services.ratelimiter;

public interface RateLimiter {
    boolean isAllowed(String key);
}
