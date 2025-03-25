package com.qmasters.fila_flex.tertSevice;

import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.exception.TooLateToChangeException;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.service.AppointmentService;
import com.qmasters.fila_flex.service.QueueService;

import com.qmasters.fila_flex.model.ENUM.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QueueService queueService;

    private AppointmentDTO appointmentDTO;
    private AppointmentType appointmentType;
    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        appointmentType = new AppointmentType();
        appointmentType.setName("Consultation");
        appointmentType.setDescription("Consultation with a doctor");
        appointmentType.setPrice(100.0);
        appointmentType.setEstimatedTime(30);
        appointmentType.setCategory(List.of("General"));
        appointmentType.setRequiredDocumentation(List.of("ID"));

        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setName("Test User");

        appointmentDTO = new AppointmentDTO(appointmentType, user, LocalDateTime.now().plusDays(1), LocalDateTime.now());
    }

    @Test
    public void testSaveAppointment() {
        Appointment appointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));

        when(queueService.assignQueuePosition(ArgumentMatchers.any(Appointment.class))).thenReturn(appointment);
        when(appointmentRepository.save(ArgumentMatchers.any(Appointment.class))).thenReturn(appointment);

        Appointment savedAppointment = appointmentService.saveAppointment(appointmentDTO);

        assertNotNull(savedAppointment);
        assertEquals("Test User", savedAppointment.getUser().getName());
        assertEquals(AppointmentStatus.MARKED, savedAppointment.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    public void testUpdateAppointmentSuccess() {
        Appointment existingAppointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
        existingAppointment.setId(1L);
        existingAppointment.setCreatedDateTime(LocalDateTime.now().minusDays(1));
        existingAppointment.setStatus(AppointmentStatus.MARKED);
        
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(ArgumentMatchers.any(Appointment.class))).thenReturn(existingAppointment);

        AppointmentDTO updatedAppointmentDTO = new AppointmentDTO();
        updatedAppointmentDTO.setScheduledDateTime(LocalDateTime.now().plusDays(2));  // Nova data

        Appointment updatedAppointment = appointmentService.updateAppointment(1L, updatedAppointmentDTO);

        // Verifica se o agendamento foi atualizado corretamente
        assertNotNull(updatedAppointment);
        assertEquals(updatedAppointmentDTO.getScheduledDateTime(), updatedAppointment.getScheduledDateTime());
        assertEquals(AppointmentStatus.MARKED, updatedAppointment.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    public void testUpdateAppointmentTooLateToChange() {
        Appointment existingAppointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
        existingAppointment.setId(1L);
        existingAppointment.setCreatedDateTime(LocalDateTime.now().minusDays(1));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));

        AppointmentDTO updatedAppointmentDTO = new AppointmentDTO();
        updatedAppointmentDTO.setScheduledDateTime(LocalDateTime.now().plusHours(5));  // Nova data com menos de 12h

        TooLateToChangeException exception = assertThrows(TooLateToChangeException.class, () ->
                appointmentService.updateAppointment(1L, updatedAppointmentDTO)
        );

        assertEquals("Só é possível reagendar uma consulta com pelo menos 12 horas de antecedência.", exception.getMessage());
    } 
    
    @Test
    public void testFindByScheduledDateTime() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(appointmentRepository.findByScheduledDateTime(start, end)).thenReturn(List.of(new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1))));

        List<Appointment> appointments = appointmentService.findByScheduledDateTime(start, end);

        assertNotNull(appointments);
        assertFalse(appointments.isEmpty());
        verify(appointmentRepository, times(1)).findByScheduledDateTime(start, end);
    }
}
