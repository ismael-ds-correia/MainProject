package com.qmasters.fila_flex.testController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.qmasters.fila_flex.controller.AuthController;
import com.qmasters.fila_flex.dto.AuthDTO;
import com.qmasters.fila_flex.dto.LoginResponseDTO;
import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.service.AuthService;
import com.qmasters.fila_flex.service.TokenService;
import com.qmasters.fila_flex.util.UserRole;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthService authService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthController authController;

    private AuthDTO authDTO;
    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        // Inicializando objetos para os testes
        authDTO = new AuthDTO();
        authDTO.setEmail("test@example.com");
        authDTO.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");
        userDTO.setRole(UserRole.USER); // Utilizando o enum correto: UserRole
        userDTO.setName("Test User");

        user = new User("test@example.com", "encodedPassword", UserRole.USER, "Test User");
        user.setId(1L);
    }

    @Test
    void testLoginSuccess() {
        // Simula o processo de autenticação
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("dummy-token");

        // Chama o método login
        ResponseEntity<LoginResponseDTO> response = authController.login(authDTO);

        // Verifica o status da resposta e o token retornado
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("dummy-token", response.getBody().getToken());
    }

    @Test
    void testRegister() {
        // Simula o registro de um usuário
        when(authService.register(userDTO)).thenReturn(user);

        // Chama o método register
        ResponseEntity<User> response = authController.register(userDTO);

        // Verifica se o status é 200 OK e se o usuário retornado é o esperado
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testLogoutWithBearerToken() {
        // Simula a requisição de logout com token válido no formato "Bearer {token}"
        String token = "dummy-token";
        String headerToken = "Bearer " + token;

        // Chama o método logout
        ResponseEntity<String> response = authController.logout(headerToken);

        // Verifica se o método de revogação de token foi chamado e se a resposta é a esperada
        verify(tokenService).revokeToken(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout realizado com sucesso", response.getBody());
    }
    
    @Test
    void testLogoutWithoutBearerToken() {
        // Caso o header não esteja no formato esperado, o token não é revogado.
        String headerToken = "invalid-token-format";

        // Chama o método logout
        ResponseEntity<String> response = authController.logout(headerToken);

        // Verifica que o método de revogação não foi chamado
        verify(tokenService, never()).revokeToken(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout realizado com sucesso", response.getBody());
    }

    @Test
    void testLogoutWithNullToken() {
        // Chama o método logout passando um token nulo
        ResponseEntity<String> response = authController.logout(null);

        // Verifica que o método de revogação de token NÃO foi chamado
        verify(tokenService, never()).revokeToken(any());

        // Garante que a resposta ainda é 200 OK e que a mensagem é a esperada
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout realizado com sucesso", response.getBody());
    }
}