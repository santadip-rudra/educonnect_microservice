package com.ctx.api_gateway.filters;

import com.ctx.api_gateway.dto.AuthRequestDto;
import com.ctx.api_gateway.dto.AuthResponseDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component // 1. Crucial for Spring to detect the filter
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("authUrl", "forwardHeaders");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Get Header
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header"));
            }

            return webClientBuilder.build()
                    .get()
                    .uri(config.getAuthUrl())
                    .header(HttpHeaders.AUTHORIZATION, authHeader) // Pass the token to Auth Service
                    .retrieve()
                    // 3. Handle 401/403 from Auth Service specifically
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication Failed"))
                    )
                    .bodyToMono(AuthResponseDto.class)
                    .flatMap(dto -> {
                        return chain.filter(exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .header("X-User-Id", userDto.getUserId() != null ? userDto.getUserId().toString() : "")
                                        .header("X-User-Role", userDto.getRole())
                                        .header("X-User-username",userDto.getUsername())
                                        .build())
                                .build());
                    });
        };
    }

    @Data
    public static class Config {
        private String authUrl;
        private Boolean forwardHeaders;
    }
}