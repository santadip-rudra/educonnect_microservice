package com.ctx.report_service.secuirity;

import com.ctx.report_service.dto.auth_principal.CurrentUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public class HeaderFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String userId   = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String role     = exchange.getRequest().getHeaders().getFirst("X-User-role");
        String username = exchange.getRequest().getHeaders().getFirst("X-User-username");

        if (userId == null || role == null) {
            return chain.filter(exchange);
        }

        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(UUID.fromString(userId));
        currentUser.setRole(role);
        currentUser.setUsername(username);

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(currentUser, null, List.of(authority));

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
    }
}