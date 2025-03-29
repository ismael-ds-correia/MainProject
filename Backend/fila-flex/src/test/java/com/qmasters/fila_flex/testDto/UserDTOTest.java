package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.*;

import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.util.UserRole;
import org.junit.jupiter.api.Test;

class UserDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Criando um objeto UserDTO com o construtor parametrizado
        UserDTO user = new UserDTO("user@example.com", "password123", UserRole.ADMIN, "John Doe");

        // Verificações
        assertEquals("user@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testSetters() {
        // Criando um objeto UserDTO com o construtor padrão
        UserDTO user = new UserDTO();

        // Definindo valores com os setters
        user.setEmail("newuser@example.com");
        user.setPassword("newpassword");
        user.setRole(UserRole.USER);
        user.setName("Jane Doe");

        // Verificações
        assertEquals("newuser@example.com", user.getEmail());
        assertEquals("newpassword", user.getPassword());
        assertEquals(UserRole.USER, user.getRole());
        assertEquals("Jane Doe", user.getName());
    }
}
