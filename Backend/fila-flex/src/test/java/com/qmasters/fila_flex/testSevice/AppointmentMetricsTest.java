package com.qmasters.fila_flex.testSevice;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.qmasters.fila_flex.dto.MetricsDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.service.AppointmentMetrics;

class AppointmentMetricsTest {

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private AppointmentMetrics appointmentMetrics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentMetrics = new AppointmentMetrics(appointmentRepository, appointmentTypeRepository);
    }

    @Test
    void testGenerateMetrics_withNoAppointments() {
        // Mocking the repository response to return no appointments
        AppointmentType appointmentType = mock(AppointmentType.class);
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList());

        assertThrows(NoSuchElementException.class, () -> {
            appointmentMetrics.generateMetrics("Consulta", null, null);
        });
    }

    @Test
    void testGenerateMetrics_withValidAppointments() {
        // Mock de AppointmentType
        AppointmentType appointmentType = mock(AppointmentType.class);
        when(appointmentType.getName()).thenReturn("Consulta");
        
        // Mock de AppointmentRepository e AppointmentTypeRepository
        AppointmentTypeRepository appointmentTypeRepository = mock(AppointmentTypeRepository.class);
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
    
        // Mock de agendamento 1
        Appointment appointment1 = mock(Appointment.class);
        when(appointment1.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0)); // Check-in às 10:00
        when(appointment1.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 10)); // Início do atendimento às 10:10 (10 minutos de espera)
        when(appointment1.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));  // Fim do atendimento às 10:40
    
        // Mock de agendamento 2
        Appointment appointment2 = mock(Appointment.class);
        when(appointment2.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0)); // Check-in às 11:00
        when(appointment2.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 12)); // Início do atendimento às 11:12 (12 minutos de espera)
        when(appointment2.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 42));  // Fim do atendimento às 11:42
    
        // Adicionando os agendamentos ao tipo de agendamento
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));
    
        // Calculando as métricas
        AppointmentMetrics appointmentMetrics = new AppointmentMetrics(mock(AppointmentRepository.class), appointmentTypeRepository);
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null); // Teste sem intervalo de datas (todos os agendamentos)
    
        // Asserting valores esperados
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());  // Esperado 2 agendamentos
        assertEquals(11, metrics.getAverageWaitingTime());          // Esperado tempo médio de espera: (10 + 12) / 2 = 11
        assertEquals(30, metrics.getAverageServiceTime());         // Esperado tempo médio de atendimento, ajuste conforme necessário
    }
    
    

    @Test
    void testGenerateMetrics_withNoValidAppointments() {
        // Mocking the repository response with appointments but no valid check-in/start times
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointment1 = mock(Appointment.class);
        Appointment appointment2 = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

        // Mocking invalid times
        when(appointment1.getCheckInTime()).thenReturn(null);
        when(appointment1.getStartTime()).thenReturn(null);

        when(appointment2.getCheckInTime()).thenReturn(null);
        when(appointment2.getStartTime()).thenReturn(null);

        // Generating metrics with invalid data
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);

        // Verifying results
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        assertEquals(0, metrics.getAverageWaitingTime());
        assertEquals(0, metrics.getAverageServiceTime());
    }

    @Test
    void testGenerateMetrics_withDateRange() {
        // Same setup as above, but applying a date range filter
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointment1 = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1));

        LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 11, 0);

        // Mocking appointment within the date range
        when(appointment1.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(appointment1.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(appointment1.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 15));
        when(appointment1.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 45));

        // Generating metrics
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", startDate, endDate);

        // Verifying results
        assertEquals(1, metrics.getTotalAppointmentsCompleteds());
        assertEquals(15, metrics.getAverageWaitingTime());
        assertEquals(30, metrics.getAverageServiceTime());
    }
}
