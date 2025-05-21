package com.netwrkly.auth.filter;

import com.netwrkly.auth.service.JwtService;
import com.netwrkly.auth.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/verify",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    );
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UserService userService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Skip authentication for public endpoints
        if (isPublicPath(request.getRequestURI(), request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        token = authHeader.substring(7);
        try {
            // Verify Firebase token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            userEmail = decodedToken.getEmail();
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("Error verifying Firebase token", e);
        }
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String requestUri, HttpServletRequest request) {
        // Only GET requests to /api/briefs are public
        if (requestUri.equals("/api/briefs") && request.getMethod().equals("GET")) {
            return true;
        }
        // Check if the path is in PUBLIC_PATHS
        return PUBLIC_PATHS.stream().anyMatch(requestUri::endsWith);
    }
} 