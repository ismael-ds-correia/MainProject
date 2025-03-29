/*package com.qmasters.fila_flex.testController;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.controller.UserController;
import com.qmasters.fila_flex.dto.response_dto.UserResponseDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.service.UserService;
import com.qmasters.fila_flex.util.UserRole;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings({ "deprecation", "null" })
    @Test
    void testGetAllUsers_Success() {
        // Arrange
        User user1 = new User("john@example.com", "password123", UserRole.USER, "John Doe");
        User user2 = new User("jane@example.com", "password456", UserRole.USER, "Jane Doe");
        when(userService.findAll()).thenReturn(List.of(user1, user2));

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getName());
        verify(userService, times(1)).findAll();
    }

   
    @Test
    void testGetUserById_Found() {
        // Arrange
        User user = new User("john@example.com", "password123", UserRole.ADMIN, "John Doe");
    
        // Mock do serviço retornando o User
        when(userService.findById(1L)).thenReturn(user);
    
        // Act
        ResponseEntity<UserResponseDTO> response = userController.getUserById(1L);
    
        // Assert
        assertNotNull(response);
        // Verificar se o corpo da resposta não é nulo antes de acessar
        assertNotNull(response.getBody(), "O corpo da resposta não pode ser nulo.");
        assertEquals(200, response.getStatusCode().value());
        assertEquals("John Doe", response.getBody().getName());
        verify(userService, times(1)).findById(1L);
    }
    
    
    
    @Test
    void testGetUserById_NotFound() {
    // Arrange
    when(userService.findById(1L)).thenReturn(null);

    // Act & Assert (capturando a exceção e validando o resultado)
    NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
        () -> userController.getUserById(1L)
    );

    assertEquals("Usuário não encontrado", exception.getMessage());
    verify(userService, times(1)).findById(1L);
}


    @SuppressWarnings("deprecation")
    @Test
    void testDeleteUserById_Success() {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act
        ResponseEntity<String> response = userController.deleteUserById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Usuário removido com sucesso", response.getBody());
        verify(userService, times(1)).deleteUser(1L);
    }

    @SuppressWarnings("deprecation")
    @Test
    void testDeleteUserById_UserNotFound() {
        // Arrange
        doThrow(new IllegalArgumentException("Usuário não encontrado, remoção não foi realizada"))
                .when(userService).deleteUser(1L);

        // Act
        ResponseEntity<String> response = userController.deleteUserById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Usuário não encontrado, remoção não foi realizada", response.getBody());
        verify(userService, times(1)).deleteUser(1L);
    }
}*/
