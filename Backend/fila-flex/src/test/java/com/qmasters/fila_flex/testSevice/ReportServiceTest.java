package com.qmasters.fila_flex.testSevice;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.qmasters.fila_flex.service.ReportService; // Importing the ReportService class

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository; // Mock do repositório

    @InjectMocks
    private ReportService reportService; // ReportService sendo injetado com o mock

    @Test
    void testGetAppointmentsByPeriod() {
        // Setup: Criando uma lista mockada de agendamentos
        LocalDateTime startDate = LocalDateTime.of(2023, 3, 1, 0, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 3, 7, 23, 59, 59, 999999);

        Appointment appointment = new Appointment();
        appointment.setScheduledDateTime(LocalDateTime.of(2023, 3, 3, 10, 0, 0, 0));
        appointment.setId(1L);

        when(appointmentRepository.findByScheduledDateTime(startDate, endDate))
                .thenReturn(List.of(appointment));

        // Chama o método de serviço
        List<Appointment> appointments = reportService.getAppointmentsByPeriod(startDate, endDate);

        // Verificando o comportamento esperado
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        assertEquals(appointment, appointments.get(0));

        // Verificando que o repositório foi chamado com os parâmetros corretos
        verify(appointmentRepository, times(1)).findByScheduledDateTime(startDate, endDate);
    }
}
