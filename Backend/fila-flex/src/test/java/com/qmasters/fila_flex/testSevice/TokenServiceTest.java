/*package com.qmasters.fila_flex.testSevice;

import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import com.qmasters.fila_flex.service.TokenService;
import static org.junit.jupiter.api.Assertions.*;
import com.qmasters.fila_flex.repository.RevokedTokenRepository;

@ExtendWith(MockitoExtension.class)  
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user@example.com", "password", UserRole.USER, "John Doe");
    }

    @Test
    void testGenerateToken() {
        String token = tokenService.generateToken(user);
        assertNotNull(token);
        assertTrue(token.contains("FilaFlex"));
        assertTrue(token.contains(user.getEmail()));
    }

    @Test
    void testValidateToken() {
        String token = tokenService.generateToken(user);
        String subject = tokenService.validateToken(token);
        assertEquals(user.getEmail(), subject);
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid_token";
        String result = tokenService.validateToken(invalidToken);
        assertEquals("Erro no validateToken, em TokenService", result);
    }

    @Test
    void testRevokeToken() {
        String token = "valid_token";
        when(revokedTokenRepository.existsByToken(token)).thenReturn(false);

        tokenService.revokeToken(token);

        verify(revokedTokenRepository, times(1)).save(any());
    }

    @Test
    void testIsTokenRevoked() {
        String token = "valid_token";
        when(revokedTokenRepository.existsByToken(token)).thenReturn(true);

        boolean isRevoked = tokenService.isTokenRevoked(token);

        assertTrue(isRevoked);
    }
}
*/