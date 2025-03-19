package com.qmasters.fila_flex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.qmasters.fila_flex.model.User; // Add this import
import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.service.UserService;
import com.qmasters.fila_flex.util.UserRole;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        // Criando um usuário de teste com todos os campos obrigatórios
        userRepository.deleteAll();// Limpa o banco de dados de teste
        user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("securepassword"); // Adicionando senha obrigatória
        user.setRole(UserRole.USER); // Definindo a role obrigatória
        userRepository.save(user); // Salvando no banco de dados de teste
    }

    @Test
    public void testFindAll() {
        List<User> users = userService.findAll();
        
        assertNotNull(users);
        assertFalse(users.isEmpty()); // Garante que a lista não está vazia
    }

    @Test
    public void testFindById() {
        User foundUser = userService.findById(user.getId());
        
        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getName());
    }

    @Test
    public void testFindByEmail() {
        User foundUser = (User) userService.findByEmail("test@example.com");
        
        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getName());
    }

    @Test
    @Transactional
    public void testDeleteUser_Success() {
        Long userId = user.getId();
        
        userService.deleteUser(userId);
        
        assertFalse(userRepository.existsById(userId)); // Verifica que o usuário foi deletado
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(999L); // Tentativa de deletar um usuário que não existe
        });

        assertEquals("Usuário não encontrado, remoção não foi realizada", exception.getMessage());
    }

    @Test
    public void testUpdate() {
        user.setName("Updated User");
        User updatedUser = userService.update(user);
        
        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
    }
}

