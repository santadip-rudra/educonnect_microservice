package com.ctx.report_service.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    private WebClient.Builder withAuth(WebClient.Builder builder, String baseUrl) {
        return builder.clone()  // ← clone so each bean gets its own builder instance
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter((request, next) -> next.exchange(
                        ClientRequest.from(request)
                                .header("Authorization",
                                        request.headers().getFirst("Authorization"))
                                .build()
                ));
    }

    @Bean
    public WebClient courseServiceClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return withAuth(loadBalancedWebClientBuilder, "http://course-service").build();
    }

    @Bean
    public WebClient userServiceClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return withAuth(loadBalancedWebClientBuilder, "http://user-management-service").build();
    }

    @Bean
    public WebClient assessmentServiceClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return withAuth(loadBalancedWebClientBuilder, "http://assessment-service").build();
    }
}