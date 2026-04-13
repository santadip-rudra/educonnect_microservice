package com.ctx.user_management_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/student/**",
                                "/user/register"
                        )
                        .permitAll() // Open the door for student lookups
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable) // Stop the HTML redirect
                .httpBasic(AbstractHttpConfigurer::disable); // Disable basic auth popups

        return http.build();
    }
}