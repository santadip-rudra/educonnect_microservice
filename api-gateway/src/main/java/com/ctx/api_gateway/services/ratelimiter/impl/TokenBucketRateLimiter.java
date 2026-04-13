package com.ctx.api_gateway.services.ratelimiter.impl;

import com.ctx.api_gateway.services.ratelimiter.RateLimiter;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int refillTokens;
    private final int refillPeriodInSeconds;
    private final ConcurrentHashMap<String,Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int capacity, int refillTokens, int refillPeriodInSeconds){
        this.capacity=capacity;
        this.refillTokens=refillTokens;
        this.refillPeriodInSeconds=refillPeriodInSeconds;
    }

    @Override
    public boolean isAllowed(String key) {
        //if(true) return true;
        Bucket bucket = buckets.computeIfAbsent(key,k-> new Bucket(capacity, Instant.now().getEpochSecond()));
        synchronized (bucket){
            long now = Instant.now().getEpochSecond();
            long elapsed = now - bucket.lastRefillTime;
            // if elapsed time is more than the refill time period we need to refill
            if(elapsed>=this.refillPeriodInSeconds){
                int refill = (int)(elapsed/refillPeriodInSeconds)*refillTokens;
                int newTokens = Math.min(bucket.tokens.get()+refill,capacity);
                bucket.tokens.set(newTokens);
                bucket.lastRefillTime=now;
            }
            if(bucket.tokens.get()>0){
                bucket.tokens.decrementAndGet();
                return true;
            }
            return false;
        }
    }

    private static class Bucket{
        AtomicInteger tokens;
        long lastRefillTime;

        Bucket(int tokens, long lastRefillTime){
            this.tokens = new AtomicInteger(tokens);
            this.lastRefillTime=lastRefillTime;
        }
    }
}
