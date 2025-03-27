package com.qmasters.fila_flex.testSevice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.qmasters.fila_flex.dto.MetricsDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.service.AppointmentMetrics;

@ExtendWith(MockitoExtension.class)
class AppointmentMetricsTest {

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @InjectMocks
    private AppointmentMetrics appointmentMetrics;

    private AppointmentType appointmentType;
    private List<Appointment> appointments;

    @BeforeEach
    void setUp() {
        appointmentType = new AppointmentType();
        appointmentType.setName("Consulta");

        Appointment ap1 = new Appointment();
        ap1.setCheckInTime(LocalDateTime.of(2024, 3, 1, 9, 0));
        ap1.setStartTime(LocalDateTime.of(2024, 3, 1, 9, 15));
        ap1.setEndTime(LocalDateTime.of(2024, 3, 1, 9, 45));
        ap1.setScheduledDateTime(LocalDateTime.of(2024, 3, 1, 8, 30));
        
        Appointment ap2 = new Appointment();
        ap2.setCheckInTime(LocalDateTime.of(2024, 3, 2, 10, 0));
        ap2.setStartTime(LocalDateTime.of(2024, 3, 2, 10, 30));
        ap2.setEndTime(LocalDateTime.of(2024, 3, 2, 11, 0));
        ap2.setScheduledDateTime(LocalDateTime.of(2024, 3, 2, 9, 45));
        
        appointments = List.of(ap1, ap2);
        appointmentType.setAppointments(appointments);
    }

    @Test
    void shouldGenerateMetricsSuccessfully() {
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);
        
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        assertEquals(22, metrics.getAverageWaitingTime()); // Média de (15 + 30) / 2
        assertEquals(30, metrics.getAverageServiceTime()); // Média de (30 + 30) / 2
    }

    @Test
    void shouldThrowExceptionWhenAppointmentTypeNotFound() {
        when(appointmentTypeRepository.findByName("Exame")).thenReturn(Optional.empty());
        
        assertThrows(NoSuchElementException.class, () -> appointmentMetrics.generateMetrics("Exame", null, null));
    }

    @Test
    void shouldThrowExceptionWhenNoAppointmentsInPeriod() {
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 2, 0, 0);
        
        assertThrows(NoSuchElementException.class, () -> appointmentMetrics.generateMetrics("Consulta", startDate, endDate));
    }
}
