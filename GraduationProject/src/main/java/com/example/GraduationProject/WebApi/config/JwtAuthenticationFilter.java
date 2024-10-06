package com.example.GraduationProject.WebApi.config;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.GraduationProject.Core.Repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT from the Authorization header
        jwt = authHeader.substring(7);
        try {
            // Extract the username from the JWT
            username = jwtService.extractUsername(jwt);

            // Proceed only if username is valid and no authentication is set
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Check if the token exists in the repository
                var token = tokenRepository.findByToken(jwt).orElse(null);
                if (token != null) {
                    // Invalidate the token if it has expired
                    if (jwtService.isTokenExpired(jwt)) {
                        token.setExpired(true);
                        token.setRevoked(true);
                        tokenRepository.save(token);
                    } else if (!token.isExpired() && !token.isRevoked() && jwtService.isTokenValid(jwt, userDetails)) {
                        // Authenticate the user if the token is valid
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT format: " + jwt, e);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
