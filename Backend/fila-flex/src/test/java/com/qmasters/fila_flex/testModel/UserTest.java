package com.qmasters.fila_flex.testModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.util.UserRole;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "password123", UserRole.USER, "Test User");
    }

    @Test
    void testUserGettersAndSetters() {
        user.setId(1L);
        assertEquals(1L, user.getId());
        
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());
        
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
        
        user.setName("New Name");
        assertEquals("New Name", user.getName());
        
        user.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testGetAuthorities_UserRoleUser() {
        List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) user.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGetAuthorities_UserRoleAdmin() {
        user.setRole(UserRole.ADMIN);
        List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) user.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGetUsernameReturnsEmail() {
        assertEquals(user.getEmail(), user.getUsername());
    }
}

