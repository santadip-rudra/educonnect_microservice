package com.ctx.api_gateway.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateLimitConfig {
    private String routeId;
    private String algorithm;
    private int capacity;
    private int refillTokens;
    private int refillPeriodInSeconds;
}
