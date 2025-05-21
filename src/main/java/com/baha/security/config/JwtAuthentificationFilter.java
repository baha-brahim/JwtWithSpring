package com.baha.security.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain // As of my understanding there can be a chain of filters ,like rings ,one after the other ,and this specifies the next one 
        ) throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization"); // The JWT token is stored in the Authorization attribute in the header
            final String jwt ;
            final String userEmail;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) { // The JWT token always starts with 'Bearer '
                filterChain.doFilter(request ,response);
                return ;
            }
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUserName(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { // This is used to check if the user is authenticated and if the token is valid
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); 
                if (jwtService.isTokenValid(jwt, userDetails)) { // This is a security best practic , re-checking userEmail==userDetails, explicit verification rather than implicit trust
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null,
                        userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    filterChain.doFilter(request ,response);
                }
            }
        }
}
