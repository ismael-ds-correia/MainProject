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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qmasters.fila_flex.controller.AppointmentTypeController;
import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.service.AppointmentTypeService;

@ExtendWith(MockitoExtension.class)
public class AppointmentTypeControllerTest {

    @Mock
    private AppointmentTypeService appointmentTypeService;

    @InjectMocks
    private AppointmentTypeController appointmentTypeController;

    private ObjectMapper objectMapper;
    private AppointmentType appointmentType;
    private AppointmentTypeDTO appointmentTypeDTO;
    private AppointmentTypeDetails appointmentTypeDetails;
    private Adress adress;

    @BeforeEach
    void setUp() {
        // Verifica se os mocks foram injetados corretamente
        assertNotNull(appointmentTypeService, "appointmentTypeService não foi injetado corretamente");
        assertNotNull(appointmentTypeController, "appointmentTypeController não foi injetado corretamente");
        
        // Inicializa o ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configuração dos dados de teste
        // Cria um endereço com parâmetros válidos (ajuste conforme a sua implementação)
        adress = new Adress("1234", "Rua Cumbuca", "Cidade A", "Estado C", "Estado D");

        // Cria os detalhes do tipo de agendamento
        appointmentTypeDetails = new AppointmentTypeDetails(
                "Consulta", 
                "Consulta com médico", 
                List.of("General"), 
                100.0, 
                LocalDate.now(), 
                List.of("ID")
        );
        
        // Instancia AppointmentType com os detalhes, tempo estimado e endereço
        appointmentType = new AppointmentType();
        appointmentType.setAppointmentTypeDetails(appointmentTypeDetails);
        appointmentType.setEstimatedTime(30);
        appointmentType.setAdress(adress);
        appointmentType.setId(1L);
        appointmentType.setAppointments(new ArrayList<>());
        appointmentType.setEvaluations(new ArrayList<>());
        
        // Cria o DTO
        appointmentTypeDTO = new AppointmentTypeDTO(appointmentTypeDetails, 30, adress);
    }

    // 1. Teste do método listAll()
    @Test
    void testListAll_Success() {
        // Stub para o método listAll() do service
        when(appointmentTypeService.listAll()).thenReturn(List.of(appointmentType));

        ResponseEntity<List<AppointmentType>> response = appointmentTypeController.listAll();

        // Verificações
        assertNotNull(response, "A resposta não deve ser nula");
        assertEquals(200, response.getStatusCode().value(), "Código de status esperado: 200");
        assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals(1, response.getBody().size(), "A lista retornada deve ter 1 elemento");
        assertEquals("Consulta", response.getBody().get(0).getAppointmentTypeDetails().getName(), 
                     "O nome do AppointmentType deve ser 'Consulta'");
                     
        verify(appointmentTypeService, times(1)).listAll();
    }
    // 2. Teste do método saveAppointmentType()
    @Test
    void testSaveAppointmentType_Success() {
        when(appointmentTypeService.saveAppointmentType(any(AppointmentTypeDTO.class))).thenReturn(appointmentType);

        ResponseEntity<AppointmentType> response = appointmentTypeController.saveAppointmentType(appointmentTypeDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Consulta", response.getBody().getAppointmentTypeDetails().getName());
        verify(appointmentTypeService, times(1)).saveAppointmentType(appointmentTypeDTO);
    }

    // 3. Teste do método findById()
    @Test
    void testFindById_Success() {
        when(appointmentTypeService.findById(1L)).thenReturn(Optional.of(appointmentType));

        ResponseEntity<Optional<AppointmentType>> response = appointmentTypeController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isPresent(), "O corpo da resposta deve conter um AppointmentType");
        assertEquals("Consulta", response.getBody().get().getAppointmentTypeDetails().getName(), 
                     "O nome do AppointmentType deve ser 'Consulta'");
        verify(appointmentTypeService, times(1)).findById(1L);

    }

    @Test
    void testFindById_NotFound() {
        when(appointmentTypeService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> appointmentTypeController.findById(1L));
    }

    // 4. Teste do método findByCategory()
    @Test
    void testFindByCategory_Success() {
        when(appointmentTypeService.findByCategory("General")).thenReturn(List.of(appointmentType));

        ResponseEntity<List<AppointmentType>> response = appointmentTypeController.findByCategory("General");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Consulta", response.getBody().get(0).getAppointmentTypeDetails().getName());
        verify(appointmentTypeService, times(1)).findByCategory("General");
    }

    @Test
    void testFindByCategory_NotFound() {
        when(appointmentTypeService.findByCategory("NonExisting")).thenReturn(List.of());

        assertThrows(NoSuchElementException.class, () -> appointmentTypeController.findByCategory("NonExisting"));
    }

    // 5. Teste do método findByPriceBetween()
    @Test
    void testFindByPriceBetween_Success() {
        when(appointmentTypeService.findByPriceBetween(50.0, 150.0)).thenReturn(List.of(appointmentType));

        ResponseEntity<List<AppointmentType>> response = appointmentTypeController.findByPriceBetween(50.0, 150.0);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Consulta", response.getBody().get(0).getAppointmentTypeDetails().getName());
        verify(appointmentTypeService, times(1)).findByPriceBetween(50.0, 150.0);
    }

    @Test
    void testFindByPriceBetween_NotFound() {
        when(appointmentTypeService.findByPriceBetween(200.0, 300.0)).thenReturn(List.of());

        assertThrows(NoSuchElementException.class, () -> appointmentTypeController.findByPriceBetween(200.0, 300.0));
    }

    // 6. Teste do método findAllByEstimatedTime()
    @Test
    void testFindAllByEstimatedTime_Success() {
        when(appointmentTypeService.findAllByOrderByEstimatedTimeAsc()).thenReturn(List.of(appointmentType));

        ResponseEntity<List<AppointmentType>> response = ResponseEntity.ok(appointmentTypeController.findAllByEstimatedTime());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Consulta", response.getBody().get(0).getAppointmentTypeDetails().getName());
        verify(appointmentTypeService, times(1)).findAllByOrderByEstimatedTimeAsc();
    }

    // 7. Teste do método findByName()
    @Test
    void testFindByName_Success() {
        when(appointmentTypeService.findByName("Consulta")).thenReturn(Optional.of(appointmentType));

        ResponseEntity<Optional<AppointmentType>> response = appointmentTypeController.findByName("Consulta");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isPresent());
        assertEquals("Consulta", response.getBody().get().getAppointmentTypeDetails().getName());
        verify(appointmentTypeService, times(1)).findByName("Consulta");
    }

    @Test
    void testFindByName_NotFound() {
        when(appointmentTypeService.findByName("NonExisting")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> appointmentTypeController.findByName("NonExisting"));
    }

    // 8. Teste do método deleteByName()
    @Test
    void testDeleteByName_Success() {
        doNothing().when(appointmentTypeService).deleteByName("Consulta");

        ResponseEntity<String> response = appointmentTypeController.deleteByName("Consulta");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Tipo de agendamento removido com sucesso", response.getBody());
        verify(appointmentTypeService, times(1)).deleteByName("Consulta");
    }

    @Test
    void testDeleteByName_NotFound() {
        doThrow(new NoSuchElementException("Tipo de agendamento não encontrado")).when(appointmentTypeService).deleteByName("NonExisting");

        ResponseEntity<String> response = appointmentTypeController.deleteByName("NonExisting");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    // 9. Teste do método deleteById()
    @Test
    void testDeleteById_Success() {
        doNothing().when(appointmentTypeService).deleteAppointmentType(1L);

        ResponseEntity<String> response = appointmentTypeController.delete(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Tipo de agendamento removido com sucesso", response.getBody());
        verify(appointmentTypeService, times(1)).deleteAppointmentType(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        doThrow(new NoSuchElementException("Tipo de agendamento não encontrado")).when(appointmentTypeService).deleteAppointmentType(1L);

        ResponseEntity<String> response = appointmentTypeController.delete(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }
}