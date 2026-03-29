package com.ctx.assessment_service.secuirity;

import com.ctx.assessment_service.dto.user.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class HeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-role");
        String username = request.getHeader("X-User-username");


        if (
                userId != null
                        && role != null
                        && SecurityContextHolder.getContext().getAuthentication() == null
        ) {

            CurrentUser currentUser = new CurrentUser();
            currentUser.setUserId(UUID.fromString(userId));
            currentUser.setRole(role);
            currentUser.setUsername(username);

            SimpleGrantedAuthority authority
                    = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(authority));

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        filterChain.doFilter(request, response);

    }
}
