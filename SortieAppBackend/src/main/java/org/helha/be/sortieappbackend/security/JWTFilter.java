package org.helha.be.sortieappbackend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.helha.be.sortieappbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JWTFilter - Authorization Header: " + request.getHeader("Authorization"));
        System.out.println("JWTFilter - Processing request for: " + request.getRequestURI());
        String jwt = parseJWTFromHeader(request);
        if(jwt != null && jwtUtils.validateToken(jwt)){
            System.out.println("JWTFilter - Valid token found: " + jwt);
            Claims claims = jwtUtils.parseToken(jwt);
            String username = claims.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(userDetails != null){
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities())
                );
                System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());
            }else {
                System.out.println("JWTFilter - UserDetailsService returned null for username: " + username);
            }
        }else {
            System.out.println("JWTFilter - No valid JWT token found");
        }
        System.out.println("JWTFilter - SecurityContext Authentication: " +
                SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request,response);
    }

    private String parseJWTFromHeader(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer ")){
            return authorization.substring(7);
        }
        return null;
    }
}

