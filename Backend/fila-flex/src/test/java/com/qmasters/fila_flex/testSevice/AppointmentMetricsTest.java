package com.qmasters.fila_flex.testSevice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.qmasters.fila_flex.dto.MetricsDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.service.AppointmentMetrics;

class AppointmentMetricsTest {

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;


    private AppointmentMetrics appointmentMetrics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentMetrics = new AppointmentMetrics(appointmentTypeRepository);
    }

    @Test
    void testGenerateMetrics_noAppointmentsForType() {
        // Simula um tipo de agendamento sem nenhum agendamento
        AppointmentType appointmentType = mock(AppointmentType.class);
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList());

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            appointmentMetrics.generateMetrics("Consulta", null, null);
        });
        assertEquals("Não foram encontrados agendamentos para este tipo.", thrown.getMessage());
    }

    @Test
    void testGenerateMetrics_withValidAppointments() {
        // Cenário básico com agendamentos válidos
        AppointmentType appointmentType = mock(AppointmentType.class);
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));

        Appointment appointment1 = mock(Appointment.class);
        when(appointment1.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(appointment1.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 10));
        when(appointment1.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));

        Appointment appointment2 = mock(Appointment.class);
        when(appointment2.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));
        when(appointment2.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 12));
        when(appointment2.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 42));

        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);

        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        // Tempo de espera: (10 + 12) / 2 = 11 minutos
        // Tempo de serviço: (30 + 30) / 2 = 30 minutos
        assertEquals(11, metrics.getAverageWaitingTime());
        assertEquals(30, metrics.getAverageServiceTime());

        verify(appointmentTypeRepository).findByName("Consulta");
        verify(appointmentType).getAppointments();
    }

    @Test
    void testGenerateMetrics_withDateRange() {
        // Cenário com filtro de data onde o agendamento está dentro do intervalo
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointment = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment));

        LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 11, 0);

        when(appointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(appointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(appointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 15));
        when(appointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 45));

        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", startDate, endDate);

        assertEquals(1, metrics.getTotalAppointmentsCompleteds());
        assertEquals(15, metrics.getAverageWaitingTime());
        assertEquals(30, metrics.getAverageServiceTime());
    }

    @Test
    void testGenerateMetrics_noAppointmentsAfterDateFilter() {
        // Todos os agendamentos estão fora do intervalo de datas definido
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointment1 = mock(Appointment.class);
        Appointment appointment2 = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

        when(appointment1.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 20, 10, 0));
        when(appointment2.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 21, 11, 0));

        LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 10, 0);

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            appointmentMetrics.generateMetrics("Consulta", startDate, endDate);
        });
        assertEquals("Não foram encontrados agendamentos no período especificado.", thrown.getMessage());
    }

    @Test
    void testGenerateMetrics_filteringOutAppointmentsWithNullScheduledDate() {
        // Cenário onde alguns agendamentos têm scheduledDateTime nulo e devem ser ignorados no filtro
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment validAppointment = mock(Appointment.class);
        Appointment nullScheduledAppointment = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        // A lista contém um agendamento com data válida e outro com data nula
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(validAppointment, nullScheduledAppointment));

        LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 11, 0);

        when(validAppointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(validAppointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(validAppointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 15));
        when(validAppointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 45));

        // Para o agendamento com data nula, os demais campos podem ter valores, mas ele será ignorado no filtro
        when(nullScheduledAppointment.getScheduledDateTime()).thenReturn(null);
        when(nullScheduledAppointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(nullScheduledAppointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 15));
        when(nullScheduledAppointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 45));

        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", startDate, endDate);

        // Apenas o agendamento com data válida deverá ser considerado
        assertEquals(1, metrics.getTotalAppointmentsCompleteds());
        assertEquals(15, metrics.getAverageWaitingTime());
        assertEquals(30, metrics.getAverageServiceTime());
    }

    @Test
    void testGenerateMetrics_withNoValidAppointments() {
        // Cenário com agendamentos que não possuem dados para cálculo de tempos (check-in e start nulos)
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointment1 = mock(Appointment.class);
        Appointment appointment2 = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

        when(appointment1.getCheckInTime()).thenReturn(null);
        when(appointment1.getStartTime()).thenReturn(null);
        when(appointment1.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));

        when(appointment2.getCheckInTime()).thenReturn(null);
        when(appointment2.getStartTime()).thenReturn(null);
        when(appointment2.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 40));

        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);

        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        assertEquals(0, metrics.getAverageWaitingTime());
        assertEquals(0, metrics.getAverageServiceTime());
    }

    @Test
    void testGenerateMetrics_withZeroWaitingAndServiceTime() {
        // Cenário em que check-in, início e fim são iguais, resultando em 0 minutos para ambos os cálculos
        AppointmentType appointmentType = mock(AppointmentType.class);
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));

        Appointment appointmentZeroTimes = mock(Appointment.class);
        LocalDateTime scheduled = LocalDateTime.of(2025, 3, 28, 12, 0);
        when(appointmentZeroTimes.getScheduledDateTime()).thenReturn(scheduled);
        when(appointmentZeroTimes.getCheckInTime()).thenReturn(scheduled);
        when(appointmentZeroTimes.getStartTime()).thenReturn(scheduled);
        when(appointmentZeroTimes.getEndTime()).thenReturn(scheduled);

        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointmentZeroTimes));

        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);

        assertEquals(1, metrics.getTotalAppointmentsCompleteds());
        assertEquals(0, metrics.getAverageWaitingTime());
        assertEquals(0, metrics.getAverageServiceTime());

        verify(appointmentTypeRepository).findByName("Consulta");
        verify(appointmentType).getAppointments();
    }

    @Test
    void testGenerateMetrics_appointmentTypeNotFound() {
        // Caso em que o repositório não encontra o AppointmentType
        when(appointmentTypeRepository.findByName("Inexistente")).thenReturn(Optional.empty());
        
        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            appointmentMetrics.generateMetrics("Inexistente", null, null);
        });
        assertEquals("Tipo de agendamento não encontrado.", thrown.getMessage());
    }

    @Test
    void testGenerateMetrics_allAppointmentsWithNullScheduledDate() {
        // Cenário: com filtro de data, todos os agendamentos possuem scheduledDateTime nulo,
        // fazendo com que o filtro descarte todos e dispare a exceção.
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appt1 = mock(Appointment.class);
        Appointment appt2 = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appt1, appt2));

        LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 11, 0);

        when(appt1.getScheduledDateTime()).thenReturn(null);
        when(appt2.getScheduledDateTime()).thenReturn(null);

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            appointmentMetrics.generateMetrics("Consulta", startDate, endDate);
        });
        assertEquals("Não foram encontrados agendamentos no período especificado.", thrown.getMessage());
    }

    @Test
    void testGenerateMetrics_mixedValidAndInvalidWaitingTimes() {
        // Cenário: lista de agendamentos em que apenas um tem dados válidos para tempo de espera.
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment validAppointment = mock(Appointment.class);
        Appointment invalidWaitingAppointment = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(validAppointment, invalidWaitingAppointment));

        // validAppointment possui check-in e start definidos
        when(validAppointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(validAppointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 10));
        when(validAppointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));
        when(validAppointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        
        // invalidWaitingAppointment tem check-in nulo (logo, não contribui para waiting time)
        when(invalidWaitingAppointment.getCheckInTime()).thenReturn(null);
        when(invalidWaitingAppointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));
        when(invalidWaitingAppointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 30));
        when(invalidWaitingAppointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));

        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);
        
        // Total de agendamentos: 2 (conta todos, mesmo os que não contribuem nos cálculos)
        // Para waiting time: somente validAppointment é considerado (10 minutos)
        // Para service time: ambos podem contribuir se tiverem dados válidos; invalidWaitingAppointment possui start/end válidos: (11:30 - 11:00) = 30 minutos.
        // Média service = (30 + 30) / 2 = 30 minutos.
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        assertEquals(10, metrics.getAverageWaitingTime());
        assertEquals(30, metrics.getAverageServiceTime());
    }

    @Test
    void testGenerateMetrics_mixedValidAndInvalidServiceTimes() {
        // Cenário: lista de agendamentos em que apenas um tem dados válidos para tempo de serviço.
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment validAppointment = mock(Appointment.class);
        Appointment invalidServiceAppointment = mock(Appointment.class);

        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(validAppointment, invalidServiceAppointment));

        // validAppointment com dados completos para waiting e service
        when(validAppointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(validAppointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 10));
        when(validAppointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));
        when(validAppointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));

        // invalidServiceAppointment: faltando endTime, logo, não deve contribuir para service time,
        // mas possui check-in e start para contribuir no cálculo do waiting time.
        when(invalidServiceAppointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));
        when(invalidServiceAppointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 10));
        when(invalidServiceAppointment.getEndTime()).thenReturn(null);
        when(invalidServiceAppointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));

        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);
        
        // Para waiting time: ambos agendamentos contribuem: 
        // validAppointment: (10:10 - 10:00) = 10 minutos; invalidServiceAppointment: (11:10 - 11:00) = 10 minutos;
        // Média waiting = (10+10)/2 = 10.
        // Para service time: somente validAppointment é considerado (30 minutos).
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        assertEquals(10, metrics.getAverageWaitingTime());
        assertEquals(30, metrics.getAverageServiceTime());
    }

    @Test
    void testGenerateMetrics_withPartialDateRange() {
        // Cenário: Apenas um dos parâmetros de data é fornecido,
        // logo, o filtro de período não é executado.
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointment = mock(Appointment.class);
    
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment));
    
        LocalDateTime scheduled = LocalDateTime.of(2025, 3, 28, 10, 0);
        when(appointment.getScheduledDateTime()).thenReturn(scheduled);
        when(appointment.getCheckInTime()).thenReturn(scheduled);
        when(appointment.getStartTime()).thenReturn(scheduled.plusMinutes(5));
        when(appointment.getEndTime()).thenReturn(scheduled.plusMinutes(25));
    
        // Apenas startDate é não nulo; endDate é null, assim o filtro não é aplicado
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", scheduled, null);
    
        assertEquals(1, metrics.getTotalAppointmentsCompleteds());
        // Tempo de espera: 5 minutos (startTime - checkInTime)
        // Tempo de serviço: 20 minutos (endTime - startTime)
        assertEquals(5, metrics.getAverageWaitingTime());
        assertEquals(20, metrics.getAverageServiceTime());
    }
    
    @Test
    void testGenerateMetrics_withDateRangeBoundary() {
        // Cenário: agendamentos com scheduledDateTime exatamente nos limites do período
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointmentAtStart = mock(Appointment.class);
        Appointment appointmentAtEnd = mock(Appointment.class);
    
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointmentAtStart, appointmentAtEnd));
    
        LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 11, 0);
    
        // Agendamento exatamente na data de início
        when(appointmentAtStart.getScheduledDateTime()).thenReturn(startDate);
        when(appointmentAtStart.getCheckInTime()).thenReturn(startDate);
        when(appointmentAtStart.getStartTime()).thenReturn(startDate.plusMinutes(10));
        when(appointmentAtStart.getEndTime()).thenReturn(startDate.plusMinutes(40));
    
        // Agendamento exatamente na data de fim
        when(appointmentAtEnd.getScheduledDateTime()).thenReturn(endDate);
        when(appointmentAtEnd.getCheckInTime()).thenReturn(endDate);
        when(appointmentAtEnd.getStartTime()).thenReturn(endDate.plusMinutes(5));
        when(appointmentAtEnd.getEndTime()).thenReturn(endDate.plusMinutes(35));
    
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", startDate, endDate);
    
        // Total de agendamentos: 2
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        // Tempo de espera: (10 minutos + 5 minutos) / 2 = 7 (divisão inteira)
        assertEquals(7, metrics.getAverageWaitingTime());
        // Tempo de serviço: (30 minutos + 30 minutos) / 2 = 30
        assertEquals(30, metrics.getAverageServiceTime());
    }

    @Test
    void testGenerateMetrics_appointmentAfterDateRange() {
        // Cenário: agendamento com scheduledDateTime após o intervalo de datas informado.
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment appointment = mock(Appointment.class);
    
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        // Agendamento com scheduledDateTime depois do endDate
        when(appointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 29, 10, 0));
        // Mesmo que os outros tempos sejam válidos, o agendamento será filtrado
        when(appointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 29, 10, 0));
        when(appointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 29, 10, 15));
        when(appointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 29, 10, 45));
    
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment));
    
        LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 11, 0);
    
        // Como o único agendamento está fora do período, espera-se a exceção:
        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            appointmentMetrics.generateMetrics("Consulta", startDate, endDate);
        });
        assertEquals("Não foram encontrados agendamentos no período especificado.", thrown.getMessage());
    }

    @Test
    void testGenerateMetrics_withNullStartTimeForWaitingTime() {
        // Cenário: lista de agendamentos em que um tem check-in válido mas startTime nulo,
        // logo, não contribui para o cálculo do tempo de espera.
        AppointmentType appointmentType = mock(AppointmentType.class);
        Appointment validAppointment = mock(Appointment.class);
        Appointment invalidAppointment = mock(Appointment.class);
    
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
        // Ambos os agendamentos possuem scheduledDateTime válidos
        when(validAppointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(invalidAppointment.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));
    
        // validAppointment possui dados completos para waiting time
        when(validAppointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(validAppointment.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 10));
        when(validAppointment.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));
        
        // invalidAppointment possui check-in válido, mas startTime nulo (não contribui para waiting time)
        when(invalidAppointment.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));
        when(invalidAppointment.getStartTime()).thenReturn(null);
        // Para o cálculo de service time, invalidAppointment pode ter start/end válidos ou não;
        // aqui definimos ambos nulos para focar no waiting time.
        when(invalidAppointment.getEndTime()).thenReturn(null);
    
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(validAppointment, invalidAppointment));
    
        // Sem filtro de datas, o método usa todos os agendamentos para o cálculo.
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);
        
        // totalAppointmentsCompleteds conta os dois agendamentos.
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        // Apenas validAppointment contribui para o waiting time: (10:10 - 10:00) = 10 minutos.
        // A média é calculada sobre 1 agendamento, resultando em 10 minutos.
        assertEquals(10, metrics.getAverageWaitingTime());
        // Para service time, somente validAppointment tem dados completos: 30 minutos.
        assertEquals(30, metrics.getAverageServiceTime());
    }

}