package com.qmasters.fila_flex.testDto;

import com.qmasters.fila_flex.dto.RegisterRequest;
import com.qmasters.fila_flex.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // Inicializando o objeto RegisterRequest antes de cada teste
        registerRequest = new RegisterRequest();
    }

    @Test
    void testSetGetName() {
        String name = "John Doe";
        registerRequest.setName(name);
        assertEquals(name, registerRequest.getName(), "O nome deveria ser 'John Doe'.");
    }

    @Test
    void testSetGetEmail() {
        String email = "john.doe@example.com";
        registerRequest.setEmail(email);
        assertEquals(email, registerRequest.getEmail(), "O email deveria ser 'john.doe@example.com'.");
    }

    @Test
    void testSetGetPassword() {
        String password = "securePassword123";
        registerRequest.setPassword(password);
        assertEquals(password, registerRequest.getPassword(), "A senha deveria ser 'securePassword123'.");
    }

    @Test
    void testSetGetRole() {
        UserRole role = UserRole.ADMIN;
        registerRequest.setRole(role);
        assertEquals(role, registerRequest.getRole(), "O papel deveria ser 'ADMIN'.");
    }

    @Test
    void testConstructor() {
        // Testando o comportamento do construtor
        RegisterRequest newRequest = new RegisterRequest();
        newRequest.setName("Alice");
        newRequest.setEmail("alice@example.com");
        newRequest.setPassword("password123");
        newRequest.setRole(UserRole.USER);

        assertEquals("Alice", newRequest.getName());
        assertEquals("alice@example.com", newRequest.getEmail());
        assertEquals("password123", newRequest.getPassword());
        assertEquals(UserRole.USER, newRequest.getRole());
    }
}
