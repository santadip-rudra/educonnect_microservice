package com.ctx.api_gateway.filters;

import com.ctx.api_gateway.config.RateLimitConfig;
import com.ctx.api_gateway.services.ratelimiter.RateLimiter;
import com.ctx.api_gateway.services.ratelimiter.impl.TokenBucketRateLimiter;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimiterGatewayFilterFactory.Config> {

    private final Map<String,RateLimiter> limiters = new ConcurrentHashMap<>();

    public RateLimiterGatewayFilterFactory(){
        super(Config.class);
        limiters.put("user_management_service", new TokenBucketRateLimiter(10,10,60));
        limiters.put("student_registry_service", new TokenBucketRateLimiter(5,5,60));
        limiters.put("course_service", new TokenBucketRateLimiter(5,5,60));
        limiters.put("assessment_attachment", new TokenBucketRateLimiter(5,5,60));
        limiters.put("assessment_service", new TokenBucketRateLimiter(10,10,60));
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
              String routeId = config.routeId;
              RateLimiter rateLimiter = limiters.get(routeId);
              if(rateLimiter==null || rateLimiter.isAllowed(getKey(exchange))){
                  return  chain.filter(exchange);
              }else{
                  exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                  return  exchange.getResponse().setComplete();
              }
        };
    }

    private String getKey(ServerWebExchange exchange){
        return  Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
    }


    @Data
    public static class Config{
        private String routeId;
    }
}
