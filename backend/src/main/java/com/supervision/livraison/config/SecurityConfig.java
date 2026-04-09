package com.supervision.livraison.config;

import com.supervision.livraison.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration with JWT authentication.
 * Configures role-based access control for CONTROLEUR and LIVREUR roles.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF since we're using stateless JWT
            .csrf(AbstractHttpConfigurer::disable)
            // Enable CORS for Android app communication
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Stateless session management (no server-side session)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                // Livraison endpoints — both roles can access
                .requestMatchers("/api/livraisons/**").hasAnyRole("CONTROLEUR", "LIVREUR")
                // Dashboard — controleur only
                .requestMatchers("/api/dashboard/**").hasRole("CONTROLEUR")
                // Personnel — both roles
                .requestMatchers("/api/personnel/**").hasAnyRole("CONTROLEUR", "LIVREUR")
                // Notifications — both roles
                .requestMatchers("/api/notifications/**").hasAnyRole("CONTROLEUR", "LIVREUR")
                // GPS — both roles
                .requestMatchers("/api/gps/**").hasAnyRole("CONTROLEUR", "LIVREUR")
                // Proof upload — both roles
                .requestMatchers("/api/proof/**").hasAnyRole("CONTROLEUR", "LIVREUR")
                // AI endpoints — both roles
                .requestMatchers("/api/ai/**").hasAnyRole("CONTROLEUR", "LIVREUR")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // Add JWT filter before the standard authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS configuration to allow requests from the Android app.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
