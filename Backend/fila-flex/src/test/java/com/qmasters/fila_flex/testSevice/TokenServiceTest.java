package com.qmasters.fila_flex.testSevice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.qmasters.fila_flex.model.RevokedToken;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.RevokedTokenRepository;
import com.qmasters.fila_flex.service.TokenService;
import com.qmasters.fila_flex.util.UserRole;

class TokenServiceTest {

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @InjectMocks
    private TokenService tokenService;

    private final String secret = "minhaChaveSecreta1234567890"; // deve ter tamanho adequado para HMAC256
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configura o secret manualmente
        ReflectionTestUtils.setField(tokenService, "secret", secret);

        // Cria um usuário fictício para os testes
        user = new User("teste@exemplo.com", "senhaEncriptada", UserRole.USER, "Nome Teste");
        // Supondo que o ID seja gerado ou definido para o teste
        user.setId(1L);

        // Define comportamento padrão para o revokedTokenRepository
        when(revokedTokenRepository.existsByToken(any(String.class))).thenReturn(false);
    }

    @Test
    void testGenerateToken_Success() {
        // Gera o token e verifica se ele não é nulo
        String token = tokenService.generateToken(user);
        assertNotNull(token);

        // Valida o token usando a mesma secret para confirmar que o subject (email) está presente
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String subject = JWT.require(algorithm)
                .withIssuer("FilaFlex")
                .build()
                .verify(token)
                .getSubject();

        assertEquals(user.getEmail(), subject);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Gera um token válido
        String token = tokenService.generateToken(user);
        String subject = tokenService.validateToken(token);
        assertEquals(user.getEmail(), subject);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Token mal formado ou com assinatura incorreta
        String invalidToken = "tokenInvalido";
        String retorno = tokenService.validateToken(invalidToken);
        assertEquals("Erro no validateToken, em TokenService", retorno);
    }

    @Test
    void testRevokeToken_NewToken() {
        String token = "algumToken";
        // Simula que o token não está revogado
        when(revokedTokenRepository.existsByToken(token)).thenReturn(false);

        tokenService.revokeToken(token);

        // Verifica se o token foi salvo no repositório
        verify(revokedTokenRepository, times(1)).save(any(RevokedToken.class));
    }

    @Test
    void testRevokeToken_AlreadyRevoked() {
        String token = "algumToken";
        // Simula que o token já foi revogado
        when(revokedTokenRepository.existsByToken(token)).thenReturn(true);

        tokenService.revokeToken(token);

        // Como o token já está revogado, o save não deve ser chamado
        verify(revokedTokenRepository, never()).save(any(RevokedToken.class));
    }

    @Test
    void testIsTokenRevoked() {
        String token = "algumToken";
        when(revokedTokenRepository.existsByToken(token)).thenReturn(true);
        assertTrue(tokenService.isTokenRevoked(token));

        when(revokedTokenRepository.existsByToken(token)).thenReturn(false);
        assertFalse(tokenService.isTokenRevoked(token));
    }

    @Test
    void testExtractAllClaims_InvalidSecret() {
        // Para testar um cenário de exceção na extração dos claims,
        // podemos alterar temporariamente o secret para um valor inválido
        String token = tokenService.generateToken(user);
        // Define um secret incorreto para forçar erro na verificação dos claims
        ReflectionTestUtils.setField(tokenService, "secret", "secretInvalido");
        // Como extractClaim invoca extractAllClaims, esperamos que uma exceção seja lançada.
        assertThrows(Exception.class, () -> tokenService.extractUsername(token));
        // Restaura o secret correto
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

}