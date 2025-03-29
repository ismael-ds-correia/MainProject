package com.qmasters.fila_flex.testModel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.util.UserRole;

public class UserTest {

    @Test
    public void testUserCreation() {
        // Arrange
        User user = new User("testuser@example.com", "password123", UserRole.USER, "Test User");

        // Act & Assert
        assertNotNull(user);
        assertEquals("testuser@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
        assertEquals(UserRole.USER, user.getRole());
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testGetAuthorities_UserRole() {
        // Arrange
        User user = new User("user@example.com", "password", UserRole.USER, "Normal User");

        // Act
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) user.getAuthorities();

        // Assert
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void testGetAuthorities_AdminRole() {
        // Arrange
        User user = new User("admin@example.com", "password", UserRole.ADMIN, "Admin User");

        // Act
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) user.getAuthorities();

        // Assert
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void testGetUsername() {
        // Arrange
        User user = new User("testuser@example.com", "password", UserRole.USER, "Test User");

        // Act
        String username = user.getUsername();

        // Assert
        assertEquals("testuser@example.com", username);
    }

    @Test
    public void testAppointments() {
        // Arrange
        User user = new User("testuser@example.com", "password", UserRole.USER, "Test User");

        // Act
        user.setAppointments(List.of());  // Passa uma lista vazia de compromissos
        List<Appointment> appointments = user.getAppointments();

        // Assert
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testSetAndGetRole() {
        // Arrange
        User user = new User();
        user.setRole(UserRole.ADMIN);

        // Act & Assert
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    public void testSetAndGetId() {
        // Arrange
        User user = new User();
        user.setId(1L);

        // Act & Assert
        assertEquals(1L, user.getId());
    }

    @Test
    public void testSetAndGetName() {
        // Arrange
        User user = new User();
        user.setName("Updated User");

        // Act & Assert
        assertEquals("Updated User", user.getName());
    }

    @Test
    public void testSetAndGetEmail() {
        // Arrange
        User user = new User();
        user.setEmail("updateduser@example.com");

        // Act & Assert
        assertEquals("updateduser@example.com", user.getEmail());
    }

    @Test
    public void testSetAndGetPassword() {
        // Arrange
        User user = new User();
        user.setPassword("newpassword");

        // Act & Assert
        assertEquals("newpassword", user.getPassword());
    }
}


