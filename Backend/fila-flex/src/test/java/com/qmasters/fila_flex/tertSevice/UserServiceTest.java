package com.qmasters.fila_flex.tertSevice;
//vai precisar refazer com os padroes de codigo correto

/*package com.qmasters.fila_flex;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.service.UserService;
import com.qmasters.fila_flex.util.UserRole;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks  
    private UserService userService;

    @Mock  
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);  
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("securepassword");
        user.setRole(UserRole.USER);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));  // Correção do tipo
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));  // Correção aqui
        when(userRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    void testFindAll() {
        List<User> users = userService.findAll();
        assertNotNull(users);
        assertFalse(users.isEmpty()); 
        verify(userRepository, times(1)).findAll(); 
    }

    @Test
    void testFindById() {
        User foundUser = userService.findById(1L);
        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByEmail() {
        User foundUser = userService.findByEmail("test@example.com").orElse(null);  // Ajustado para Optional
        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getName());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testDeleteUser_Success() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);  
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(999L));
        assertEquals("Usuário não encontrado, remoção não foi realizada", exception.getMessage());
    }

    @Test
    void testUpdate() {
        user.setName("Updated User");
        when(userRepository.save(user)).thenReturn(user);
        User updatedUser = userService.update(user);
        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
    }
}
*/