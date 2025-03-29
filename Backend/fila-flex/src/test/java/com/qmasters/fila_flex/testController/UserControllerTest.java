package com.qmasters.fila_flex.testController;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.controller.UserController;
import com.qmasters.fila_flex.dto.response_dto.UserResponseDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.service.UserService;
import com.qmasters.fila_flex.util.UserRole;

public class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    public void testGetAllUsers_Success() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        user1.setRole(UserRole.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.USER);

        List<User> users = Arrays.asList(user1, user2);
        when(userService.findAll()).thenReturn(users);

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetUserById_UserFound() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setRole(UserRole.USER);

        UserResponseDTO userResponseDTO = new UserResponseDTO(user, Arrays.asList()); // Supondo que não há compromissos

        when(userService.findById(userId)).thenReturn(user);
        when(userService.getUserWithAppointments(userId)).thenReturn(userResponseDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        assertEquals("Test User", response.getBody().getName());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userService.findById(userId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            userController.getUserById(userId);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    public void testDeleteUserById_Success() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // Act
        ResponseEntity<String> response = userController.deleteUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário removido com sucesso", response.getBody());
    }

    @Test
    public void testDeleteUserById_UserNotFound() {
        // Arrange
        Long userId = 1L;
        doThrow(new IllegalArgumentException("Usuário não encontrado")).when(userService).deleteUser(userId);

        // Act
        ResponseEntity<String> response = userController.deleteUserById(userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Usuário não encontrado", response.getBody());
    }
}

