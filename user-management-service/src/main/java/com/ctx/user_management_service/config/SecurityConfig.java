package com.ctx.user_management_service.config;

import com.ctx.user_management_service.secuirity.HeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new HeaderFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(
                        s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}