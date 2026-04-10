package com.ctx.course_service.clientCall.intreceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();

            // 1. Forward the Bearer Token (Good for general security)
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authHeader);
            }

            // 2. Forward the Custom Headers (Required for your HeaderFilter)
            forwardHeader("X-User-Id", request, requestTemplate);
            forwardHeader("X-User-role", request, requestTemplate);
            forwardHeader("X-User-username", request, requestTemplate);
        }
    }

    private void forwardHeader(String headerName, HttpServletRequest request, RequestTemplate template) {
        String headerValue = request.getHeader(headerName);
        if (headerValue != null) {
            template.header(headerName, headerValue);
        }
    }

}
