package com.qmasters.fila_flex.testSevice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.qmasters.fila_flex.dto.AppointmentTypeDetailsDTO;
import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.repository.AppointmentTypeDetailsRepository;
import com.qmasters.fila_flex.service.AppointmentTypeDetailsService;
import com.qmasters.fila_flex.service.CategoryService;

class AppointmentTypeDetailsServiceTest {

    @Mock
    private AppointmentTypeDetailsRepository appointmentTypeDetailsRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private AppointmentTypeDetailsService appointmentTypeDetailsService;

    private AppointmentTypeDetails appointmentTypeDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inicializa o AppointmentTypeDetails com valores fictícios
        appointmentTypeDetails = new AppointmentTypeDetails(
            "Consulta Psicológica",
            "Sessão com psicólogo",
            Arrays.asList("Saúde Mental"),
            100.00,
            LocalDate.now(),
            Arrays.asList("RG", "CPF")
        );

        // Adiciona a verificação para garantir que appointmentTypeDetails não seja null
        assertNotNull(appointmentTypeDetails, "appointmentTypeDetails should not be null after initialization");
    }

    @Test
    void testSaveAppointmentTypeDetails() {
        // Dados de entrada
        AppointmentTypeDetailsDTO dto = new AppointmentTypeDetailsDTO();
        dto.setName("Consulta Psicológica");
        dto.setDescription("Sessão com psicólogo");
        dto.setCategory(Arrays.asList("Saúde Mental"));
        dto.setPrice(100.00); // Usa um double diretamente
        dto.setAppointmentDate(LocalDate.now());
        dto.setRequiredDocumentation(Arrays.asList("RG", "CPF"));

        AppointmentTypeDetails expectedAppointmentTypeDetails = new AppointmentTypeDetails(
            dto.getName(),
            dto.getDescription(),
            dto.getCategory(),
            dto.getPrice(),
            dto.getAppointmentDate(),
            dto.getRequiredDocumentation()
        );

        // Simulação do comportamento dos mocks
        when(appointmentTypeDetailsRepository.save(any(AppointmentTypeDetails.class)))
            .thenReturn(expectedAppointmentTypeDetails);

        // Execução do método de serviço
        AppointmentTypeDetails result = appointmentTypeDetailsService.saveAppointmentTypeDetails(dto);

        // Verificações
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getCategory(), result.getCategory());
        assertEquals(dto.getPrice(), result.getPrice());
        assertEquals(dto.getAppointmentDate(), result.getAppointmentDate());
        assertEquals(dto.getRequiredDocumentation(), result.getRequiredDocumentation());

        // Verifica se o método save foi chamado uma vez
        verify(appointmentTypeDetailsRepository, times(1)).save(any(AppointmentTypeDetails.class));

        // Verifica se o serviço de categorias foi chamado para salvar a categoria
        verify(categoryService, times(1)).saveCategory(any(CategoryDTO.class));
    }

    @Test
    void testListAll() {
        // Dados simulados
        AppointmentTypeDetails appointment1 = new AppointmentTypeDetails(
            "Consulta A", "Desc A", Arrays.asList("Categoria A"), 200.00,
            LocalDate.now(), Arrays.asList("Doc A")
        );

        AppointmentTypeDetails appointment2 = new AppointmentTypeDetails(
            "Consulta B", "Desc B", Arrays.asList("Categoria B"), 300.00,
            LocalDate.now(), Arrays.asList("Doc B")
        );

        List<AppointmentTypeDetails> appointments = Arrays.asList(appointment1, appointment2);

        // Simulação do comportamento do repositório
        when(appointmentTypeDetailsRepository.findAll()).thenReturn(appointments);

        // Execução do método
        List<AppointmentTypeDetails> result = appointmentTypeDetailsService.listAll();

        // Verificações
        assertEquals(2, result.size());
        assertEquals("Consulta A", result.get(0).getName());
        assertEquals("Consulta B", result.get(1).getName());

        // Verifica se o método do repositório foi chamado uma vez
        verify(appointmentTypeDetailsRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        Long id = 1L;
        AppointmentTypeDetails appointment = new AppointmentTypeDetails(
            "Consulta", "Desc", Arrays.asList("Categoria"), 250.00,
            LocalDate.now(),
            Arrays.asList("Doc")
        );

        when(appointmentTypeDetailsRepository.findById(id)).thenReturn(Optional.of(appointment));

        Optional<AppointmentTypeDetails> result = appointmentTypeDetailsService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("Consulta", result.get().getName());

        verify(appointmentTypeDetailsRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        Long id = 99L;

        when(appointmentTypeDetailsRepository.findById(id)).thenReturn(Optional.empty());

        Optional<AppointmentTypeDetails> result = appointmentTypeDetailsService.findById(id);

        assertFalse(result.isPresent());

        verify(appointmentTypeDetailsRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteAppointmentTypeDetails_Success() {
        Long id = 1L;

        when(appointmentTypeDetailsRepository.existsById(id)).thenReturn(true);

        appointmentTypeDetailsService.deleteAppointmentTypeDetails(id);

        verify(appointmentTypeDetailsRepository, times(1)).existsById(id);
        verify(appointmentTypeDetailsRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteAppointmentTypeDetails_NotFound() {
        Long id = 99L;

        when(appointmentTypeDetailsRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(NoSuchElementException.class, () ->
            appointmentTypeDetailsService.deleteAppointmentTypeDetails(id)
        );

        assertEquals("AppointmentTypeDetails not found", exception.getMessage());

        verify(appointmentTypeDetailsRepository, times(1)).existsById(id);
        verify(appointmentTypeDetailsRepository, never()).deleteById(anyLong());
    }
}
