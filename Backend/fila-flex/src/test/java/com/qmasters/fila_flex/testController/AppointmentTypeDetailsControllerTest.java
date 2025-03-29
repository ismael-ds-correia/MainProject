package com.qmasters.fila_flex.testController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qmasters.fila_flex.controller.AppointmentTypeDetailsController;
import com.qmasters.fila_flex.dto.AppointmentTypeDetailsDTO;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.service.AppointmentTypeDetailsService;

public class AppointmentTypeDetailsControllerTest {

    @Mock
    private AppointmentTypeDetailsService appointmentTypeDetailsService;

    @InjectMocks
    private AppointmentTypeDetailsController appointmentTypeDetailsController;

    private ObjectMapper objectMapper;

    private AppointmentTypeDetailsDTO appointmentTypeDetailsDTO;
    private AppointmentTypeDetails appointmentTypeDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Criação de dados de exemplo para AppointmentTypeDetailsDTO
        appointmentTypeDetailsDTO = new AppointmentTypeDetailsDTO(
            "Consulta", 
            "Consulta com médico", 
            List.of("General"), 
            150.0, 
            LocalDate.of(2025, 3, 29), 
            List.of("ID")
        );

        // Criação de AppointmentTypeDetails para simular a entidade
        appointmentTypeDetails = new AppointmentTypeDetails(
            "Consulta", 
            "Consulta com médico", 
            List.of("General"), 
            150.0, 
            LocalDate.of(2025, 3, 29), 
            List.of("ID")
        );
        appointmentTypeDetails.setId(1L);
    }

    @Test
    void testListAll_Success() {
        when(appointmentTypeDetailsService.listAll()).thenReturn(List.of(appointmentTypeDetails));

        ResponseEntity<List<AppointmentTypeDetails>> response = appointmentTypeDetailsController.listAll();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Consulta", response.getBody().get(0).getName());
        verify(appointmentTypeDetailsService, times(1)).listAll();
    }

    @Test
    void testSaveAppointmentTypeDetails_Success() {
        when(appointmentTypeDetailsService.saveAppointmentTypeDetails(any(AppointmentTypeDetailsDTO.class))).thenReturn(appointmentTypeDetails);

        ResponseEntity<AppointmentTypeDetails> response = appointmentTypeDetailsController.saveAppointmentTypeDetails(appointmentTypeDetailsDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().getId());
        assertEquals("Consulta", response.getBody().getName());
        verify(appointmentTypeDetailsService, times(1)).saveAppointmentTypeDetails(appointmentTypeDetailsDTO);
    }

    @Test
    void testFindById_Found() {
        when(appointmentTypeDetailsService.findById(1L)).thenReturn(Optional.of(appointmentTypeDetails));

        ResponseEntity<Optional<AppointmentTypeDetails>> response = appointmentTypeDetailsController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isPresent());
        assertEquals(1L, response.getBody().get().getId());
        verify(appointmentTypeDetailsService, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(appointmentTypeDetailsService.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            appointmentTypeDetailsController.findById(1L);
        });

        assertEquals("Detalhes do Tipo de agendamento não encontrado.", exception.getMessage());
        verify(appointmentTypeDetailsService, times(1)).findById(1L);
    }

    @Test
    void testDeleteAppointmentTypeDetails_Success() {
        doNothing().when(appointmentTypeDetailsService).deleteAppointmentTypeDetails(1L);

        ResponseEntity<String> response = appointmentTypeDetailsController.deleteAppointmentTypeDetails(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Detalhes do Tipo de agendamento deletado com sucesso.", response.getBody());
        verify(appointmentTypeDetailsService, times(1)).deleteAppointmentTypeDetails(1L);
    }

    @Test
    void testDeleteAppointmentTypeDetails_NotFound() {
        doThrow(new IllegalArgumentException("ID não encontrado")).when(appointmentTypeDetailsService).deleteAppointmentTypeDetails(1L);

        ResponseEntity<String> response = appointmentTypeDetailsController.deleteAppointmentTypeDetails(1L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("ID não encontrado", response.getBody());
        verify(appointmentTypeDetailsService, times(1)).deleteAppointmentTypeDetails(1L);
    }
}
