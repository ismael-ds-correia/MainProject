package com.qmasters.fila_flex.testController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qmasters.fila_flex.controller.AppointmentController;
import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.service.AppointmentService;
import com.qmasters.fila_flex.util.PriorityCondition;
import com.qmasters.fila_flex.util.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private ObjectMapper objectMapper;

    // Dados de exemplo
    private AppointmentDTO appointmentDTO;
    private Appointment appointment;
    private AppointmentType appointmentType;
    private AppointmentTypeDetails appointmentTypeDetails;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        appointmentTypeDetails = new AppointmentTypeDetails();
        appointmentTypeDetails.setName("Consulta");
        appointmentTypeDetails.setDescription("Consulta com um médico");
        appointmentTypeDetails.setPrice(100.0);
        appointmentTypeDetails.setCategory(List.of("General"));
        appointmentTypeDetails.setRequiredDocumentation(List.of("ID"));

        appointmentType = new AppointmentType();
        appointmentType.setAppointmentTypeDetails(appointmentTypeDetails);
        appointmentType.setEstimatedTime(30);

        // Configure o usuário com id, mesmo que o DTO não o exija
        user = new User("john@example.com", "password123", UserRole.USER, "John Doe");
        user.setId(1L);

        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        appointmentDTO = new AppointmentDTO(appointmentType, user, scheduledDateTime, LocalDateTime.now());

        appointment = new Appointment(appointmentType, user, scheduledDateTime);
        appointment.setStatus(AppointmentStatus.MARKED);
        appointment.setId(1L);
    }

    @Test
    void testGetAllAppointments_Success() {
        when(appointmentService.getAllAppointment()).thenReturn(List.of(appointment));

        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointment();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getUser().getName());
        verify(appointmentService, times(1)).getAllAppointment();
    }

    @Test
    void testCreateAppointment_Success() {
        // Configura os objetos de teste
        // O DTO não precisa ter id, pois ele é usado só para entrada
        when(appointmentService.saveAppointment(any(AppointmentDTO.class))).thenReturn(appointment);

        // Aqui o appointment retornado já possui um User com id preenchido
        ResponseEntity<Appointment> response = appointmentController.createAppointment(appointmentDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        // Verifica que o appointment retornado possui um id definido, assim como o usuário.
        assertNotNull(response.getBody().getId());
        assertNotNull(response.getBody().getUser().getId());
        assertEquals(1L, response.getBody().getUser().getId());
        verify(appointmentService, times(1)).saveAppointment(appointmentDTO);
    }
    @Test
    void testUpdateAppointment_Success() {
        // Supondo que o AppointmentDTO para atualização só precisa alterar a data
        AppointmentDTO updatedDto = new AppointmentDTO();
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);
        updatedDto.setScheduledDateTime(newDate);

        when(appointmentService.updateAppointment(eq(1L), any(AppointmentDTO.class)))
                .thenReturn(appointment);

        ResponseEntity<Appointment> response = appointmentController.updateAppointment(1L, updatedDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(appointmentService, times(1)).updateAppointment(eq(1L), any(AppointmentDTO.class));
    }

    @Test
    void testGetAppointmentById_Found() {
        when(appointmentService.findAppointmentById(1L)).thenReturn(Optional.of(appointment));

        ResponseEntity<Optional<Appointment>> response = appointmentController.getAppointmentById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isPresent());
        assertEquals(1L, response.getBody().get().getId());
        verify(appointmentService, times(1)).findAppointmentById(1L);
    }

    @Test
    void testGetAppointmentById_NotFound() {
        when(appointmentService.findAppointmentById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            appointmentController.getAppointmentById(1L);
        });

        assertEquals("Agendamento não encontrado", exception.getMessage());
        verify(appointmentService, times(1)).findAppointmentById(1L);
    }

    @Test
    void testSetPriorityCondition_Success() {
        // Utilizando um valor do enum existente (por exemplo, PWD)
        appointment.setPriorityCondition(PriorityCondition.PWD);
        when(appointmentService.setPriorityCondition(eq(1L), any(PriorityCondition.class)))
                .thenReturn(appointment);

        ResponseEntity<Appointment> response = appointmentController.setPriorityCondition(1L, PriorityCondition.PWD);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(PriorityCondition.PWD.getCondition(), response.getBody().getPriorityCondition().getCondition());
        verify(appointmentService, times(1)).setPriorityCondition(eq(1L), any(PriorityCondition.class));
    }

    @Test
    void testGetAppointmentBetweenDates_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(appointmentService.findByScheduledDateTime(startDate, endDate)).thenReturn(List.of(appointment));

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentBetwenDate(startDate, endDate);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
        verify(appointmentService, times(1)).findByScheduledDateTime(startDate, endDate);
    }

    @Test
    void testDeleteAppointmentById_Success() {
        // Para simular exclusão, basta garantir que o service não lança exceção
        doNothing().when(appointmentService).deleteAppointment(1L);

        ResponseEntity<String> response = appointmentController.deleteAppointmentById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Agendamento removido com sucesso", response.getBody());
        verify(appointmentService, times(1)).deleteAppointment(1L);
    }
}
