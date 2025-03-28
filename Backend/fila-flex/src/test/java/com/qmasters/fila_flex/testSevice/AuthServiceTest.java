package com.qmasters.fila_flex.testSevice;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.service.AuthService;
import com.qmasters.fila_flex.util.UserRole;

import org.springframework.security.core.userdetails.UserDetails;

class AuthServiceTest {

    private AuthService authService;
    private UserRepository userRepositoryMock;

    @BeforeEach
    void setUp() {
        // Criando mock para o repositório
        userRepositoryMock = Mockito.mock(UserRepository.class);
        
        // Injetando o mock manualmente no AuthService
        authService = new AuthService(userRepositoryMock);
    }

    @Test
    void testRegister_WhenEmailAlreadyInUse() {
        // Dado que o e-mail já existe no repositório
        String email = "user@example.com";
        UserDTO userDTO = new UserDTO(email, "password123", UserRole.USER, "John Doe");
        
        // Simulando o comportamento do repositório
        when(userRepositoryMock.findByEmail(email)).thenReturn(new User(email, "password123", UserRole.USER, "John Doe"));
        
        // Verificando se a exceção é lançada
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(userDTO);
        });
        
        // Verificando a mensagem da exceção
        assertEquals("Email já está em uso", thrown.getMessage());
    }
    
    @Test
    void testRegister_WhenEmailNotInUse() {
        // Dado que o e-mail não existe no repositório
        String email = "newuser@example.com";
        UserDTO userDTO = new UserDTO(email, "password123", UserRole.USER, "John Doe");
    
        // Simulando o comportamento do repositório para e-mail não encontrado
        when(userRepositoryMock.findByEmail(email)).thenReturn(null);  // E-mail não existe
        
        // Simulando o comportamento do repositório ao salvar o novo usuário
        when(userRepositoryMock.save(any(User.class))).thenReturn(new User(email, "encryptedPassword", UserRole.USER, "John Doe"));
        
        // Chamando o método de registro
        User user = authService.register(userDTO);
    
        // Verificando se o usuário foi salvo corretamente
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        verify(userRepositoryMock).save(any(User.class));  // Verificando se o método save foi chamado
    }
    
    @Test
    void testLoadUserByUsername() {
        // Dado que o e-mail existe no repositório
        String email = "user@example.com";
        User user = new User(email, "encryptedPassword", UserRole.USER, "John Doe");
    
        // Simulando o comportamento do repositório
        when(userRepositoryMock.findByEmail(email)).thenReturn(user);
        
        // Chamando o método loadUserByUsername
        UserDetails userDetails = authService.loadUserByUsername(email);
    
        // Verificando se o retorno é correto
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername()); // Verificando se o e-mail é retornado
    }
    
}

