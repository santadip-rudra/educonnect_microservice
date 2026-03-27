package com.ctx.course_service.clientCall.intreceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
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
        if(requestAttributes!=null){
            String authHeader = requestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader !=null && authHeader.startsWith("Bearer ")) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION,authHeader);
            }
        }
    }
}
