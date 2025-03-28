package com.qmasters.fila_flex.testDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.MetricsDTO;

import static org.junit.jupiter.api.Assertions.*;

class MetricsDTOTest {

    private MetricsDTO metricsDTO;

    @BeforeEach
    void setUp() {
        // Inicializando o objeto MetricsDTO antes de cada teste
        metricsDTO = new MetricsDTO(100, 15, 30);
    }

    @Test
    void testGetTotalAppointmentsCompleteds() {
        assertEquals(100, metricsDTO.getTotalAppointmentsCompleteds(), 
                     "A quantidade total de agendamentos completos deveria ser 100.");
    }

    @Test
    void testGetAverageWaitingTime() {
        assertEquals(15, metricsDTO.getAverageWaitingTime(), 
                     "O tempo médio de espera deveria ser 15 minutos.");
    }

    @Test
    void testGetAverageServiceTime() {
        assertEquals(30, metricsDTO.getAverageServiceTime(), 
                     "O tempo médio de serviço deveria ser 30 minutos.");
    }

    @Test
    void testSetTotalAppointmentsCompleteds() {
        metricsDTO.setTotalAppointmentsCompleteds(200);
        assertEquals(200, metricsDTO.getTotalAppointmentsCompleteds(), 
                     "A quantidade total de agendamentos completos deveria ser atualizada para 200.");
    }

    @Test
    void testSetAverageWaitingTime() {
        metricsDTO.setAverageWaitingTime(20);
        assertEquals(20, metricsDTO.getAverageWaitingTime(), 
                     "O tempo médio de espera deveria ser atualizado para 20 minutos.");
    }

    @Test
    void testSetAverageServiceTime() {
        metricsDTO.setAverageServiceTime(40);
        assertEquals(40, metricsDTO.getAverageServiceTime(), 
                     "O tempo médio de serviço deveria ser atualizado para 40 minutos.");
    }

    @Test
    void testConstructor() {
        MetricsDTO newMetricsDTO = new MetricsDTO(50, 10, 25);
        assertEquals(50, newMetricsDTO.getTotalAppointmentsCompleteds());
        assertEquals(10, newMetricsDTO.getAverageWaitingTime());
        assertEquals(25, newMetricsDTO.getAverageServiceTime());
    }
}
