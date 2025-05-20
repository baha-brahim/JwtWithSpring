package com.baha.security.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component //
@RequiredArgsConstructor // This would make a constructor for all final fields
public class JwtAuthentificationFilter extends OncePerRequestFilter { // We used OncePerRequestFilter so that this filter fires whenever a new request is sent by the user
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain // As of my understanding there can be a chain of filters ,like rings ,one after the other ,and this specifies the next one 
        ) throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization"); // The JWT token is stored in the Authorization attribute in the header
            final String jwt ;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) { // the JWT token always starts with 'Bearer '
                filterChain.doFilter(request ,response);
                return ;
            }
            jwt = authHeader.substring(7);
            String userEmail = jwtService.extractUserName(jwt);
        }
}
