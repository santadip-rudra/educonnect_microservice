package com.ctx.student_registry_service.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
       ServletRequestAttributes requestAttr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
       if(requestAttr!=null){
           String authHeader = requestAttr.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
           if(authHeader!=null && authHeader.startsWith("Bearer ")){
               requestTemplate.header(HttpHeaders.AUTHORIZATION,authHeader);
           }
       }
    }
}
