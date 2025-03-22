package com.qmasters.fila_flex.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

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
        .authorizeHttpRequests(authorize -> {
            configureUserEndpoints(authorize);
            configureAppointmentEndpoints(authorize);
            configureAppointmentTypeEndpoints(authorize);
            configureCategoryEndpoints(authorize);
            configureAdressEndpoints(authorize);
            configureEvaluationEndpoints(authorize);

            authorize.anyRequest().authenticated();
        })
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    //==================================== Definições de Endpoints =============================================================

    private void configureUserEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
            .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/user/**").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole(ROLE_ADMIN);
    }

    private void configureAppointmentEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] appointmentEndpoint = {"/appointment/**"};

        authorize
            .requestMatchers(HttpMethod.GET, appointmentEndpoint).hasRole(ROLE_USER)
            .requestMatchers(HttpMethod.PUT, appointmentEndpoint).permitAll()
            .requestMatchers(HttpMethod.POST, appointmentEndpoint).hasRole(ROLE_USER)
            .requestMatchers(HttpMethod.DELETE, appointmentEndpoint).permitAll();
    }

    private void configureAppointmentTypeEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] appointmentTypeEndpoint = {"/appointment-types/**"};

        authorize
            .requestMatchers(HttpMethod.GET, appointmentTypeEndpoint).permitAll()
            .requestMatchers(HttpMethod.POST, appointmentTypeEndpoint).hasRole(ROLE_ADMIN)
            .requestMatchers(HttpMethod.DELETE, appointmentTypeEndpoint).hasRole(ROLE_ADMIN);
    }

    private void configureCategoryEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] categoryEndpoint = {"/category/**"};

        authorize
            .requestMatchers(HttpMethod.GET, categoryEndpoint).permitAll()
            .requestMatchers(HttpMethod.POST, categoryEndpoint).hasRole(ROLE_ADMIN);
    }

    private void configureAdressEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] adressEndpoint = {"/adress/**"};

        authorize
            .requestMatchers(HttpMethod.GET, adressEndpoint).permitAll()
            .requestMatchers(HttpMethod.POST, adressEndpoint).permitAll()
            .requestMatchers(HttpMethod.DELETE, adressEndpoint).permitAll();
    }

    private void configureEvaluationEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] evaluationEndpoint = {"/evaluations/**"};

        authorize
            .requestMatchers(HttpMethod.POST, evaluationEndpoint).permitAll()
            .requestMatchers(HttpMethod.GET, evaluationEndpoint).permitAll();
    }

    @Bean //mesmo sem ser chamada, se não for declarada aqui a autenticação não funciona
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
