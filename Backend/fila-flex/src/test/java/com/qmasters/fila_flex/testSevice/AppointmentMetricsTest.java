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
        appointmentMetrics = new AppointmentMetrics(appointmentTypeRepository);
    }

    @Test
    void testGenerateMetrics_noAppointmentsForType() {
    // Simula um tipo de agendamento sem agendamentos
    AppointmentType appointmentType = mock(AppointmentType.class);
    when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
    
    // Simulando uma lista vazia
    when(appointmentType.getAppointments()).thenReturn(Arrays.asList());

    // Esperando a exceção "Não foram encontrados agendamentos para este tipo."
    NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
        appointmentMetrics.generateMetrics("Consulta", null, null);
    });

    assertEquals("Não foram encontrados agendamentos para este tipo.", thrown.getMessage());
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
    void testGenerateMetrics_withDateRangeAndNoAppointmentsFound() {
    // Criando o mock para AppointmentType
    AppointmentType appointmentType = mock(AppointmentType.class);
    when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
    
    // Lista de agendamentos vazia, pois não existe nenhum agendamento dentro do intervalo de datas
    when(appointmentType.getAppointments()).thenReturn(Arrays.asList());
    
    // Definindo intervalo de datas
    LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 10, 0);
    
    // Executando o método e esperando a exceção
    assertThrows(NoSuchElementException.class, () -> {
        appointmentMetrics.generateMetrics("Consulta", startDate, endDate);
    });
}


    @Test
    void testGenerateMetrics_withValidAppointments() {
        // Criando mock de AppointmentType
        AppointmentType appointmentType = mock(AppointmentType.class);
        when(appointmentType.getName()).thenReturn("Consulta");
    
        // Certifique-se de que o repositório está retornando um Optional com esse objeto
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
    
        // Criando mock de agendamentos
        Appointment appointment1 = mock(Appointment.class);
        when(appointment1.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
        when(appointment1.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 10));
        when(appointment1.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));
    
        Appointment appointment2 = mock(Appointment.class);
        when(appointment2.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 0));
        when(appointment2.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 12));
        when(appointment2.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 42));
    
        // Associando os agendamentos ao tipo de agendamento
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));
    
        // Executando o método testado
        MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", null, null);
    
        // Verificando se os valores retornados são os esperados
        assertEquals(2, metrics.getTotalAppointmentsCompleteds());
        assertEquals(11, metrics.getAverageWaitingTime());
        assertEquals(30, metrics.getAverageServiceTime());
        
        // Verificando se os métodos foram chamados
        verify(appointmentTypeRepository).findByName("Consulta");
        verify(appointmentType).getAppointments();
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
    @Test
void testGenerateMetrics_noAppointmentsAfterDateFilter() {
    AppointmentType appointmentType = mock(AppointmentType.class);
    when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));

    Appointment appointment1 = mock(Appointment.class);
    Appointment appointment2 = mock(Appointment.class);

    when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

    // Todos os agendamentos têm datas fora do intervalo
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
void testGenerateMetrics_appointmentsWithNullCheckInStartTime() {
    AppointmentType appointmentType = mock(AppointmentType.class);
    when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));

    Appointment appointment1 = mock(Appointment.class);
    Appointment appointment2 = mock(Appointment.class);

    when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

    // Ambos os agendamentos têm campos nulos
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
void testGenerateMetrics_appointmentsWithNullScheduledDate() {
    AppointmentType appointmentType = mock(AppointmentType.class);
    when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));

    Appointment appointment1 = mock(Appointment.class);
    Appointment appointment2 = mock(Appointment.class);

    when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

    // Um agendamento com data nula, outro fora do intervalo
    when(appointment1.getScheduledDateTime()).thenReturn(null);
    when(appointment2.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 20, 10, 0));

    LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 10, 0);

    NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
        appointmentMetrics.generateMetrics("Consulta", startDate, endDate);
    });

    assertEquals("Não foram encontrados agendamentos no período especificado.", thrown.getMessage());
}



    @Test
    void testGenerateMetrics_withNoAppointmentsFound() {
        // Mocking no appointments found for the given appointment type
        when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(mock(AppointmentType.class)));
        
        // Simulating no appointments
        AppointmentType appointmentType = mock(AppointmentType.class);
        when(appointmentType.getAppointments()).thenReturn(Arrays.asList());

        // Executing and expecting the exception
        assertThrows(NoSuchElementException.class, () -> {
            appointmentMetrics.generateMetrics("Consulta", null, null);
        });
    }
    @Test
void testGenerateMetrics_noAppointmentsInDateRange() {
    // Simula um tipo de agendamento com alguns agendamentos
    AppointmentType appointmentType = mock(AppointmentType.class);
    Appointment appointment1 = mock(Appointment.class);
    Appointment appointment2 = mock(Appointment.class);

    when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
    when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

    // Simulando que os agendamentos estão fora do intervalo de datas
    when(appointment1.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 20, 10, 0));
    when(appointment2.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 21, 11, 0));

    // Definindo um intervalo de datas que não inclui os agendamentos simulados
    LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 10, 0);

    // Esperando a exceção "Não foram encontrados agendamentos no período especificado."
    NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
        appointmentMetrics.generateMetrics("Consulta", startDate, endDate);
    });

    assertEquals("Não foram encontrados agendamentos no período especificado.", thrown.getMessage());
}
@Test
void testGenerateMetrics_withAppointmentsInDateRange() {
    // Simula um tipo de agendamento com agendamentos
    AppointmentType appointmentType = mock(AppointmentType.class);
    Appointment appointment1 = mock(Appointment.class);
    Appointment appointment2 = mock(Appointment.class);

    when(appointmentTypeRepository.findByName("Consulta")).thenReturn(Optional.of(appointmentType));
    when(appointmentType.getAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

    // Simulando que os agendamentos estão dentro do intervalo de datas
    when(appointment1.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
    when(appointment2.getScheduledDateTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 30));

    when(appointment1.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 0));
    when(appointment1.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 15));
    when(appointment1.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 45));

    when(appointment2.getCheckInTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 30));
    when(appointment2.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 10, 40));
    when(appointment2.getEndTime()).thenReturn(LocalDateTime.of(2025, 3, 28, 11, 10));

    // Definindo um intervalo de datas que inclui os agendamentos
    LocalDateTime startDate = LocalDateTime.of(2025, 3, 28, 9, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 3, 28, 11, 0);

    // Executando o método
    MetricsDTO metrics = appointmentMetrics.generateMetrics("Consulta", startDate, endDate);

    // Verificando se os valores retornados são os esperados
    assertEquals(2, metrics.getTotalAppointmentsCompleteds());
    assertEquals(12, metrics.getAverageWaitingTime());
    assertEquals(30, metrics.getAverageServiceTime());

    // Verificando se os métodos foram chamados
    verify(appointmentTypeRepository).findByName("Consulta");
    verify(appointmentType).getAppointments();
}


}