package com.ctx.audit_log_service.config;

import com.ctx.audit_log_service.filters.HeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final HeaderFilter headerFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security){
        return  security
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->
                        auth
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(login->login.disable())
                .build();
    }
}

