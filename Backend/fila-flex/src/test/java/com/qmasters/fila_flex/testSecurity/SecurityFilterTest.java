package com.qmasters.fila_flex.testSecurity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.security.SecurityFilter;
import com.qmasters.fila_flex.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class SecurityFilterTest {

    @Mock private TokenService tokenService;
    @Mock private UserRepository userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;
    @Mock private UserDetails userDetails;

    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityFilter = new SecurityFilter(tokenService, userRepository);
        SecurityContextHolder.clearContext();
    }

    private void invokeDoFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws Exception {
        Method method = SecurityFilter.class.getDeclaredMethod("doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(securityFilter, request, response, filterChain);
    }

    @Test
    void testDoFilterInternal_NoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        
        invokeDoFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_TokenRevoked() throws Exception {
        String tokenValue = "tokenRevoked123";
        String tokenHeader = "Bearer " + tokenValue;
        when(request.getHeader("Authorization")).thenReturn(tokenHeader);
        when(tokenService.isTokenRevoked(tokenValue)).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        invokeDoFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
        writer.flush();
        assertTrue(sw.toString().contains("Token inválido"));
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        String tokenValue = "validToken123";
        String tokenHeader = "Bearer " + tokenValue;
        when(request.getHeader("Authorization")).thenReturn(tokenHeader);
        when(tokenService.isTokenRevoked(tokenValue)).thenReturn(false);
        when(tokenService.validateToken(tokenValue)).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> Collections.emptyList());

        invokeDoFilterInternal(request, response, filterChain);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userDetails, auth.getPrincipal());
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        
        verify(filterChain).doFilter(request, response);
    }

    private String invokeRecoverToken(HttpServletRequest request) throws Exception {
        Method method = SecurityFilter.class.getDeclaredMethod("recoverToken", HttpServletRequest.class);
        method.setAccessible(true);
        return (String) method.invoke(securityFilter, request);
    }

    @Test
    void testRecoverToken_NoAuthorizationHeader() throws Exception {
        String token = invokeRecoverToken(request);
        assertNull(token);
    }

    @Test
    void testRecoverToken_InvalidAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidTokenFormat");

        String token = invokeRecoverToken(request);

        assertNull(token);
    }

    @Test
    void testRecoverToken_EmptyAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("");
    
        String token = invokeRecoverToken(request);
    
        assertNull(token);
    }

}