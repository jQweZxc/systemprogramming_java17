package com.example.demo.config;

import java.util.Arrays;
import java.util.Collections;

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

import com.example.demo.jwt.JwtAuthEntryPoint;
import com.example.demo.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    // Публичные URL
    private static final String[] ALLOWED_URLS = {
        "/swagger-ui/**", 
        "/v3/api-docs/**",
        "/api/telegram-setup/**",
        "/api/telegram/**"
    };
    
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
    throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",
                "http://localhost:5500", 
                "http://127.0.0.1:8080"
            ));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(Arrays.asList("*"));
            config.setAllowCredentials(true);
            return config;
        }));
        
        http.authorizeHttpRequests(authz -> authz
            // Разрешаем ВСЕ статические ресурсы и HTML
            .requestMatchers(
                "/",
                "/index.html",
                "/login.html",
                "/app.js",
                "/styles.css",
                "/mock-api.js",
                "/favicon.ico",
                "/api/passengers/**",
                "/img/**",
                "/css/**",
                "/js/**",
                "/*.html",
                "/*.js",
                "/*.css",
                "/*.png",
                "/*.jpg"
            ).permitAll()
            
            // Разрешаем аутентификацию
            .requestMatchers("/api/auth/**").permitAll()
            
            // Разрешаем Swagger
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            
            // Разрешаем Telegram эндпоинты
            .requestMatchers("/api/telegram/**").permitAll()
            
            // Разрешаем setup endpoint
            .requestMatchers("/api/setup/**").permitAll()
            
            // ВСЕ остальное требует аутентификации
            .anyRequest().authenticated()
        );
        
        http.sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        http.exceptionHandling(exception ->
            exception.authenticationEntryPoint(jwtAuthEntryPoint));
        
        http.addFilterBefore(jwtAuthFilter, 
            UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}