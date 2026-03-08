package com.cyberassure.cyberassureproject.config;

import com.cyberassure.cyberassureproject.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                "/api/v1/auth/**",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/h2-console/**")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.POST, "/api/v1/policies/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/v1/risk-assessments/**")
                                                .hasRole("CUSTOMER")

                                                .requestMatchers(HttpMethod.POST, "/api/v1/subscriptions/**")
                                                .hasRole("CUSTOMER")

                                                .requestMatchers(HttpMethod.PUT,
                                                                "/api/v1/subscriptions/*/review")
                                                .hasRole("UNDERWRITER")

                                                .anyRequest().authenticated())

                                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                                .addFilterBefore(jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(List.of("http://localhost:4200"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}