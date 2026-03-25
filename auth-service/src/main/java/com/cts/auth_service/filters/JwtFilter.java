//package com.cts.auth_service.filters;
//
//import com.cts.auth_service.services.AuthDetailsService;
//import com.cts.auth_service.services.JwtService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtFilter extends OncePerRequestFilter {
//
//
//    private final JwtService jwtService;
//
//    private final AuthDetailsService authDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader=request.getHeader("Authorization");
//        String token=null;
//        String username=null;
//        if(authHeader!=null && authHeader.startsWith("Bearer "))
//        {
//            token=authHeader.substring(7);
//            try{
//                username=jwtService.extractUserName(token);
//            } catch (Exception e) {
//                log.debug("JWT Validation failed: {}" , e.getMessage());
//            }
//        }
//        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
//        {
//            UserDetails userDetails=authDetailsService.loadUserByUsername(username);
//            if(jwtService.validateToken(token,userDetails))
//            {
//                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request,response);
//    }
//}
