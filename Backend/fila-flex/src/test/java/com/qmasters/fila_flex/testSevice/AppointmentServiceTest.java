package com.qmasters.fila_flex.testSevice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.exception.InvalidDateRangeException;
import com.qmasters.fila_flex.exception.TooLateToChangeException;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.UserRepository;
import com.qmasters.fila_flex.service.AppointmentService;
import com.qmasters.fila_flex.service.QueueService;
import com.qmasters.fila_flex.util.PriorityCondition;

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
    void setup() {
        MockitoAnnotations.openMocks(this);

        AppointmentTypeDetails details = new AppointmentTypeDetails();
        details.setName("Consultation");
        details.setDescription("Consultation with a doctor");
        details.setPrice(100.0);
        details.setCategory(List.of("General"));
        details.setRequiredDocumentation(List.of("ID"));

        appointmentType = new AppointmentType();
        appointmentType.setAppointmentTypeDetails(details);
        appointmentType.setEstimatedTime(30);
    
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setName("Test User");
    
        appointmentDTO = new AppointmentDTO(appointmentType, user, LocalDateTime.now().plusDays(1), LocalDateTime.now());
    }

    @Test
    void testSaveAppointment() {
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
    void testUpdateAppointmentSuccess() {
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
    void testUpdateAppointmentTooLateToChange() {
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
    void testUpdateAppointmentNotFound() {
        // Configura o repository para não encontrar o appointment
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentDTO appointmentDto = new AppointmentDTO();
        // Configura uma data de agendamento válida (mais de 12 horas no futuro)
        appointmentDto.setScheduledDateTime(LocalDateTime.now().plusDays(2));

        // Verifica que é lançada a exceção com a mensagem correta
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
            appointmentService.updateAppointment(1L, appointmentDto)
        );
        assertEquals("Agendamento não encontrado.", exception.getMessage());
    }
    
    @Test
    void testFindByScheduledDateTime() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(appointmentRepository.findByScheduledDateTime(start, end)).thenReturn(List.of(new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1))));

        List<Appointment> appointments = appointmentService.findByScheduledDateTime(start, end);

        assertNotNull(appointments);
        assertFalse(appointments.isEmpty());
        verify(appointmentRepository, times(1)).findByScheduledDateTime(start, end);
    }

    @Test
    void testFindByScheduledDateTimeInvalidRange() {
        // Define um intervalo onde a data de início é após a data final
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        // Verifica que a exceção InvalidDateRangeException é lançada com a mensagem esperada
        InvalidDateRangeException exception = assertThrows(InvalidDateRangeException.class, () ->
            appointmentService.findByScheduledDateTime(start, end)
        );
        assertEquals("Data de início não pode ser posterior a data final", exception.getMessage());
    }

    @Test
    void testDeleteAppointmentSuccess() {
        // Configure o AppointmentType com um ID válido
        appointmentType.setId(100L);
        
        Appointment existingAppointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
        existingAppointment.setId(1L);
        existingAppointment.setQueueOrder(2);
    
        when(appointmentRepository.existsById(1L)).thenReturn(true);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        doNothing().when(queueService).reorganizeQueueAfterRemoval(anyLong(), anyInt());
        doNothing().when(appointmentRepository).deleteById(1L);
    
        assertDoesNotThrow(() -> appointmentService.deleteAppointment(1L));
    
        verify(queueService, times(1)).reorganizeQueueAfterRemoval(anyLong(), anyInt());
        verify(appointmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAppointmentNotFound() {
        when(appointmentRepository.existsById(1L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                appointmentService.deleteAppointment(1L)
        );

        assertEquals("Agendamento não encontrado, remoção não foi realizada", exception.getMessage());
    }

    @Test
    void testSetPriorityConditionSuccess() {
        Appointment existingAppointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
        existingAppointment.setId(1L);
    
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);
    
        Appointment updatedAppointment = appointmentService.setPriorityCondition(1L, PriorityCondition.ELDERLY);
    
        assertNotNull(updatedAppointment);
        assertEquals(PriorityCondition.ELDERLY, updatedAppointment.getPriorityCondition());
        verify(appointmentRepository, times(1)).save(existingAppointment);
        verify(queueService, times(1)).insertWithPriority(1L);
    }

    @Test
    void testSetPriorityConditionNoPriority() {
        Appointment existingAppointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
        existingAppointment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        Appointment updatedAppointment = appointmentService.setPriorityCondition(1L, PriorityCondition.NO_PRIORITY);

        assertNotNull(updatedAppointment);
        assertEquals(PriorityCondition.NO_PRIORITY, updatedAppointment.getPriorityCondition());

        verify(appointmentRepository, times(1)).save(existingAppointment);
        verify(queueService, never()).insertWithPriority(anyLong()); // <- Verifica que NÃO foi chamado
    }

    @Test
    void testSetPriorityConditionAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                appointmentService.setPriorityCondition(1L, PriorityCondition.ELDERLY)
        );

        assertEquals("Agendamento não encontrado.", exception.getMessage());
    }

    @Test
    void testFindFullAppointmentsByUserIdSuccess() {
        when(appointmentRepository.findByUserId(1L)).thenReturn(List.of(new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1))));

        List<Appointment> appointments = appointmentService.findFullAppointmentsByUserId(1L);

        assertNotNull(appointments);
        assertFalse(appointments.isEmpty());
        verify(appointmentRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testFindFullAppointmentsByUserIdThrowsExceptionWhenNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.findFullAppointmentsByUserId(null)
        );

        assertEquals("ID do usuário não pode ser nulo", exception.getMessage());
    }

    @Test
    void testFindAppointmentByIdSuccess() {
        Appointment appointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
        appointment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        Optional<Appointment> foundAppointment = appointmentService.findAppointmentById(1L);

        assertTrue(foundAppointment.isPresent());
        assertEquals(1L, foundAppointment.get().getId());
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAppointmentByIdNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Appointment> foundAppointment = appointmentService.findAppointmentById(1L);

        assertTrue(foundAppointment.isEmpty());
    }

    @Test
    void testGetAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(
                new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1)),
                new Appointment(appointmentType, user, LocalDateTime.now().plusDays(2))
        ));

        List<Appointment> appointments = appointmentService.getAllAppointment();

        assertNotNull(appointments);
        assertEquals(2, appointments.size());
        verify(appointmentRepository, times(1)).findAll();
    }

}
