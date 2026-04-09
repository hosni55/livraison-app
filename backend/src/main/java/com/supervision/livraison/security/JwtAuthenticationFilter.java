package com.supervision.livraison.security;

import com.supervision.livraison.entity.Personnel;
import com.supervision.livraison.repository.PersonnelRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter — intercepts requests, validates JWT tokens,
 * and sets the authentication context for authorized requests.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final PersonnelRepository personnelRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, PersonnelRepository personnelRepository) {
        this.jwtUtil = jwtUtil;
        this.personnelRepository = personnelRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Extract JWT from "Bearer <token>" header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("Error extracting username from JWT: " + e.getMessage());
            }
        }

        // If username is extracted and no authentication is set yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user from database
            Personnel personnel = personnelRepository.findByLogin(username).orElse(null);

            if (personnel != null && jwtUtil.validateToken(jwt, username)) {
                // Build authentication token with role authority
                String role = jwtUtil.extractRole(jwt);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                personnel,
                                null,
                                Collections.singletonList(authority)
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
