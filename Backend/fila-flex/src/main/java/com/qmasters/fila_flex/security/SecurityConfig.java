package com.qmasters.fila_flex.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
        .cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:4200", "https://fila-flex-frontend.onrender.com")); // Altere conforme necessário
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));  
            config.setAllowCredentials(true);
            config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
            return config;
        }))
        //qualquer saida API deve ser adicionada aqui
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/user/**").permitAll()
        .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("ADMIN")

        .requestMatchers(HttpMethod.GET, "/appointment-types/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/appointment-types/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/appointment-types/**").hasRole("ADMIN")

        .requestMatchers(HttpMethod.GET, "/category/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/category/**").hasRole("ADMIN")

        .requestMatchers(HttpMethod.GET, "/adress/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/adress/**").permitAll()
        .requestMatchers(HttpMethod.DELETE, "/adress/**").permitAll()

        .requestMatchers(HttpMethod.GET, "/appointment/**").hasRole("USER")
        .requestMatchers(HttpMethod.PUT, "/appointment/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/appointment/**").hasRole("USER")
        .requestMatchers(HttpMethod.DELETE, "/appointment/**").permitAll()

        .requestMatchers(HttpMethod.POST, "/evaluations/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/evaluations/**").permitAll()

        .anyRequest().authenticated())

        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean//aparentemente sem uso neste formato
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
