package com.qmasters.fila_flex.testSevice;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.qmasters.fila_flex.dto.response_dto.UserResponseDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.service.UserService;
import com.qmasters.fila_flex.util.UserRole;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, appointmentRepository);
    }

    @Test
    void testFindAll() {
        List<User> mockUsers = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> users = userService.findAll();

        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void testFindById_Success() {
        User mockUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User user = userService.findById(1L);

        assertNotNull(user);
        assertEquals(mockUser, user);
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User user = userService.findById(1L);

        assertNull(user);
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindByEmail() {
        UserDetails mockUser = mock(UserDetails.class);
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        UserDetails user = userService.findByEmail("test@example.com");

        assertNotNull(user);
        assertEquals(mockUser, user);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testGetUserWithAppointments_Success() {
        Long userId = 1L;

        // Criação de um usuário mockado
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(UserRole.USER);

        // Criação de uma lista de compromissos mockados
        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

        // Simula o comportamento dos repositórios
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(appointmentRepository.findByUserId(userId)).thenReturn(appointments);

        // Executa o método
        UserResponseDTO response = userService.getUserWithAppointments(userId);

        // Verifica os resultados
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertEquals("USER", response.getRole());
        assertEquals(2, response.getAppointments().size());
    }

    @Test
    void testGetUserWithAppointments_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            userService.getUserWithAppointments(1L);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(appointmentRepository, never()).findByUserId(anyLong());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("Usuário não encontrado, remoção não foi realizada", exception.getMessage());

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdate() {
        User mockUser = new User();
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User updatedUser = userService.update(mockUser);

        assertNotNull(updatedUser);
        assertEquals(mockUser, updatedUser);
        verify(userRepository).save(mockUser);
    }
}
