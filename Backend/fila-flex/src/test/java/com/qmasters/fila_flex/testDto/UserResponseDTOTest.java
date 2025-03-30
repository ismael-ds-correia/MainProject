package com.qmasters.fila_flex.testDto;
import com.qmasters.fila_flex.dto.response_dto.UserResponseDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.util.UserRole;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class UserResponseDTOTest {

    private User user;
    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    void setUp() {
        // Mocking the User object
        user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getName()).thenReturn("John Doe");
        when(user.getRole()).thenReturn(UserRole.USER);

        // Mocking the Appointment objects
        appointment1 = mock(Appointment.class);
        appointment2 = mock(Appointment.class);
    }

    @Test
    void testConstructor_withUserAndAppointments() {
        // Lista de agendamentos
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

        // Criando o DTO com o construtor
        UserResponseDTO userResponseDTO = new UserResponseDTO(user, appointments);

        // Verificando se o DTO foi inicializado corretamente
        assertEquals(1L, userResponseDTO.getId());
        assertEquals("user@example.com", userResponseDTO.getEmail());
        assertEquals("John Doe", userResponseDTO.getName());
        assertEquals("USER", userResponseDTO.getRole());
        assertEquals(2, userResponseDTO.getAppointments().size());
    }

    @Test
    void testSettersAndGetters() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        // Testando os setters
        userResponseDTO.setId(2L);
        userResponseDTO.setEmail("newuser@example.com");
        userResponseDTO.setName("Jane Doe");
        userResponseDTO.setRole("ADMIN");

        // Testando os getters
        assertEquals(2L, userResponseDTO.getId());
        assertEquals("newuser@example.com", userResponseDTO.getEmail());
        assertEquals("Jane Doe", userResponseDTO.getName());
        assertEquals("ADMIN", userResponseDTO.getRole());
    }

    @Test
    void testAppointmentsList() {
        // Lista de agendamentos
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

        // Criando o DTO
        UserResponseDTO userResponseDTO = new UserResponseDTO(user, appointments);

        // Verificando a lista de agendamentos
        assertNotNull(userResponseDTO.getAppointments());
        assertEquals(2, userResponseDTO.getAppointments().size());
    }
}
