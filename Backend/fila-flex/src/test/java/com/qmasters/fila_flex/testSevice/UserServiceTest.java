package com.qmasters.fila_flex.testSevice;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.service.UserService;
import com.qmasters.fila_flex.util.UserRole;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Cria um usuário de exemplo
        user = new User("user@example.com", "password", UserRole.USER, "John Doe");
        user.setId(1L);
    }

    @Test
    public void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> result = userService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testFindByIdFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.findById(1L);
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        User result = userService.findById(2L);
        assertNull(result);
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    public void testFindByEmail() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        UserDetails result = userService.findByEmail("user@example.com");
        assertNotNull(result);
        assertEquals("user@example.com", result.getUsername());
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    public void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(2L));
        assertEquals("Usuário não encontrado, remoção não foi realizada", exception.getMessage());
        verify(userRepository, times(1)).existsById(2L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testUpdateUser() {
        when(userRepository.save(user)).thenReturn(user);
        User result = userService.update(user);
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).save(user);
    }
}
