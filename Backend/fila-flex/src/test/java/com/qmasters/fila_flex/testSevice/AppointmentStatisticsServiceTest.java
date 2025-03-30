package com.qmasters.fila_flex.testSevice;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.qmasters.fila_flex.service.AppointmentStatisticsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AppointmentStatisticsServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentStatisticsService appointmentStatisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testes para getAverageWaitTime
    @Test
    void emptyList() {
        when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());
        
        double result = appointmentStatisticsService.getAverageWaitTime();
        
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void singleValidAppointment() {
        LocalDateTime checkIn = LocalDateTime.of(2025, 3, 27, 10, 0);
        LocalDateTime start = LocalDateTime.of(2025, 3, 27, 10, 30);

        Appointment appointment = new Appointment();
        appointment.setCheckInTime(checkIn);
        appointment.setStartTime(start);

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        double result = appointmentStatisticsService.getAverageWaitTime();
        
        // 30 minutos / 1 = 30 minutos
        assertEquals(30.0, result, 0.001);
    }

    @Test
    void multipleValidAppointments() {
        LocalDateTime checkIn1 = LocalDateTime.of(2025, 3, 27, 10, 0);
        LocalDateTime start1 = LocalDateTime.of(2025, 3, 27, 10, 30);
        LocalDateTime checkIn2 = LocalDateTime.of(2025, 3, 27, 11, 0);
        LocalDateTime start2 = LocalDateTime.of(2025, 3, 27, 11, 15);

        Appointment appointment1 = new Appointment();
        appointment1.setCheckInTime(checkIn1);
        appointment1.setStartTime(start1);

        Appointment appointment2 = new Appointment();
        appointment2.setCheckInTime(checkIn2);
        appointment2.setStartTime(start2);

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

        double result = appointmentStatisticsService.getAverageWaitTime();
        
        // (30 minutos + 15 minutos) / 2 = 22.5 minutos
        assertEquals(22.5, result, 0.001);
    }

    @Test
    void withNullCheckInTime() {
        Appointment validAppointment = new Appointment();
        validAppointment.setCheckInTime(LocalDateTime.of(2025, 3, 27, 10, 0));
        validAppointment.setStartTime(LocalDateTime.of(2025, 3, 27, 10, 30));

        Appointment invalidAppointment = new Appointment();
        invalidAppointment.setCheckInTime(null);
        invalidAppointment.setStartTime(LocalDateTime.of(2025, 3, 27, 11, 15));

        when(appointmentRepository.findAll()).thenReturn(List.of(validAppointment, invalidAppointment));

        double result = appointmentStatisticsService.getAverageWaitTime();
        
        // Apenas 30 minutos / 2 = 15 minutos
        assertEquals(15.0, result, 0.001);
    }

    @Test
    void withNullStartTime() {
        Appointment validAppointment = new Appointment();
        validAppointment.setCheckInTime(LocalDateTime.of(2025, 3, 27, 10, 0));
        validAppointment.setStartTime(LocalDateTime.of(2025, 3, 27, 10, 30));

        Appointment invalidAppointment = new Appointment();
        invalidAppointment.setCheckInTime(LocalDateTime.of(2025, 3, 27, 11, 0));
        invalidAppointment.setStartTime(null);

        when(appointmentRepository.findAll()).thenReturn(List.of(validAppointment, invalidAppointment));

        double result = appointmentStatisticsService.getAverageWaitTime();
        
        // Apenas 30 minutos / 2 = 15 minutos
        assertEquals(15.0, result, 0.001);
    }

    // Testes para getAverageServiceTime
    @Test
    void testGetAverageServiceTime_EmptyList() {
        when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());
        
        double result = appointmentStatisticsService.getAverageServiceTime();
        
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void singleValidAppointment_ReturnsCorrectAverage() {
        LocalDateTime start = LocalDateTime.of(2025, 3, 27, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 3, 27, 11, 0);

        Appointment appointment = new Appointment();
        appointment.setStartTime(start);
        appointment.setEndTime(end);

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        double result = appointmentStatisticsService.getAverageServiceTime();
        
        // 30 minutos / 1 = 30 minutos
        assertEquals(30.0, result, 0.001);
    }

    @Test
    void multipleValidAppointments_ReturnsCorrectAverage() {
        LocalDateTime start1 = LocalDateTime.of(2025, 3, 27, 10, 30);
        LocalDateTime end1 = LocalDateTime.of(2025, 3, 27, 11, 0);
        LocalDateTime start2 = LocalDateTime.of(2025, 3, 27, 11, 15);
        LocalDateTime end2 = LocalDateTime.of(2025, 3, 27, 11, 45);

        Appointment appointment1 = new Appointment();
        appointment1.setStartTime(start1);
        appointment1.setEndTime(end1);

        Appointment appointment2 = new Appointment();
        appointment2.setStartTime(start2);
        appointment2.setEndTime(end2);

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

        double result = appointmentStatisticsService.getAverageServiceTime();
        
        // (30 minutos + 30 minutos) / 2 = 30 minutos
        assertEquals(30.0, result, 0.001);
    }

    @Test
    void testGetAverageServiceTime_WithNullStartTime() {
        Appointment validAppointment = new Appointment();
        validAppointment.setStartTime(LocalDateTime.of(2025, 3, 27, 10, 30));
        validAppointment.setEndTime(LocalDateTime.of(2025, 3, 27, 11, 0));

        Appointment invalidAppointment = new Appointment();
        invalidAppointment.setStartTime(null);
        invalidAppointment.setEndTime(LocalDateTime.of(2025, 3, 27, 11, 45));

        when(appointmentRepository.findAll()).thenReturn(List.of(validAppointment, invalidAppointment));

        double result = appointmentStatisticsService.getAverageServiceTime();
        
        // Apenas 30 minutos / 2 = 15 minutos
        assertEquals(15.0, result, 0.001);
    }

    @Test
    void testGetAverageServiceTime_WithNullEndTime() {
        Appointment validAppointment = new Appointment();
        validAppointment.setStartTime(LocalDateTime.of(2025, 3, 27, 10, 30));
        validAppointment.setEndTime(LocalDateTime.of(2025, 3, 27, 11, 0));

        Appointment invalidAppointment = new Appointment();
        invalidAppointment.setStartTime(LocalDateTime.of(2025, 3, 27, 11, 15));
        invalidAppointment.setEndTime(null);

        when(appointmentRepository.findAll()).thenReturn(List.of(validAppointment, invalidAppointment));

        double result = appointmentStatisticsService.getAverageServiceTime();
        
        // Apenas 30 minutos / 2 = 15 minutos
        assertEquals(15.0, result, 0.001);
    }

    // Testes para getTotalAppointments
    @Test
    void testGetTotalAppointments_ZeroAppointments() {
        when(appointmentRepository.count()).thenReturn(0L);
        
        long result = appointmentStatisticsService.getTotalAppointments();
        
        assertEquals(0L, result);
    }

    @Test
    void testGetTotalAppointments_MultipleAppointments() {
        when(appointmentRepository.count()).thenReturn(42L);
        
        long result = appointmentStatisticsService.getTotalAppointments();
        
        assertEquals(42L, result);
    }
}