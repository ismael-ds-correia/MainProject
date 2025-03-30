package com.qmasters.fila_flex.testController;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.controller.MetricsController;
import com.qmasters.fila_flex.dto.MetricsDTO;
import com.qmasters.fila_flex.service.AppointmentMetrics;

class MetricsControllerTest {

    @Mock
    private AppointmentMetrics appointmentMetrics;

    private MetricsController metricsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        metricsController = new MetricsController(appointmentMetrics);
    }

    @Test
    void testGetMetricsByAppointmentType_Success() {
        // Configura um objeto de métricas simulado
        MetricsDTO mockMetrics = new MetricsDTO(2, 15, 30);
        when(appointmentMetrics.generateMetrics("Consulta", null, null)).thenReturn(mockMetrics);

        // Executa o método
        ResponseEntity<MetricsDTO> response = metricsController.getMetricsByAppointmentType("Consulta", null, null);

        // Verificações
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockMetrics, response.getBody());

        verify(appointmentMetrics).generateMetrics("Consulta", null, null);
    }

    @Test
    void testGetMetricsByAppointmentType_NotFound() {
        // Simula um erro de elemento não encontrado
        when(appointmentMetrics.generateMetrics("Consulta", null, null))
                .thenThrow(new NoSuchElementException());

        // Executa o método
        ResponseEntity<MetricsDTO> response = metricsController.getMetricsByAppointmentType("Consulta", null, null);

        // Verificações
        assertEquals(404, response.getStatusCode().value());
        assertEquals(null, response.getBody());

        verify(appointmentMetrics).generateMetrics("Consulta", null, null);
    }

    @Test
    void testGetMetricsByAppointmentType_InternalServerError() {
        // Simula um erro inesperado
        when(appointmentMetrics.generateMetrics("Consulta", null, null))
                .thenThrow(new RuntimeException("Erro inesperado"));

        // Executa o método
        ResponseEntity<MetricsDTO> response = metricsController.getMetricsByAppointmentType("Consulta", null, null);

        // Verificações
        assertEquals(500, response.getStatusCode().value());
        assertEquals(null, response.getBody());

        verify(appointmentMetrics).generateMetrics("Consulta", null, null);
    }
}