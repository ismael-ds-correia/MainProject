package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.util.UserRole;

class UserDTOTest {

    private UserDTO userDTO;

    @BeforeEach
    void setup() {
        // Inicializando o objeto UserDTO com dados de teste
        userDTO = new UserDTO("user@example.com", "password123", UserRole.ADMIN, "John Doe");
    }

    @Test
    void testConstructor() {
        assertNotNull(userDTO);
        assertEquals("user@example.com", userDTO.getEmail());
        assertEquals("password123", userDTO.getPassword());
        assertEquals(UserRole.ADMIN, userDTO.getRole());
        assertEquals("John Doe", userDTO.getName());
    }

    @Test
    void testSettersAndGetters() {
        // Testando o setter e getter para cada campo
        userDTO.setEmail("newemail@example.com");
        userDTO.setPassword("newpassword123");
        userDTO.setRole(UserRole.USER);
        userDTO.setName("Jane Doe");

        assertEquals("newemail@example.com", userDTO.getEmail());
        assertEquals("newpassword123", userDTO.getPassword());
        assertEquals(UserRole.USER, userDTO.getRole());
        assertEquals("Jane Doe", userDTO.getName());
    }

    @Test
    void testDefaultConstructor() {
        UserDTO defaultUserDTO = new UserDTO();
        assertNotNull(defaultUserDTO);
    }

    @Test
    void testGettersAndSettersForEachField() {
        // Testando o getter e setter para cada atributo individualmente
        userDTO.setEmail("newemail@example.com");
        userDTO.setPassword("newpassword123");
        userDTO.setRole(UserRole.USER);
        userDTO.setName("Jane Doe");

        assertEquals("newemail@example.com", userDTO.getEmail());
        assertEquals("newpassword123", userDTO.getPassword());
        assertEquals(UserRole.USER, userDTO.getRole());
        assertEquals("Jane Doe", userDTO.getName());
    }
}

