package com.qmasters.fila_flex.testSevice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.exception.InvalidPriceRangeException;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.service.AppointmentTypeService;
import com.qmasters.fila_flex.service.CategoryService;

@ExtendWith(MockitoExtension.class)
class AppointmentTypeServiceTest {

    @InjectMocks
    private AppointmentTypeService appointmentTypeService;

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Mock
    private CategoryService categoryService;

    private AppointmentType appointmentType;
    private AppointmentTypeDTO appointmentTypeDTO;

    @BeforeEach
    void setup() {
        AppointmentTypeDetails details = new AppointmentTypeDetails();
        details.setName("Consultation");
        details.setDescription("Doctor consultation");
        details.setPrice(100.0);
        details.setCategory(List.of("General"));

        Adress adress = new Adress("123 Main St", "City", "State", "12345", "Country");

        appointmentType = new AppointmentType(details, 30, adress);
        appointmentTypeDTO = new AppointmentTypeDTO(details, 30, adress);
    }

    @Test
    @Transactional
    void testSaveAppointmentType() {
        when(appointmentTypeRepository.save(any(AppointmentType.class))).thenReturn(appointmentType);

        AppointmentType saved = appointmentTypeService.saveAppointmentType(appointmentTypeDTO);

        assertNotNull(saved);
        assertEquals("Consultation", saved.getAppointmentTypeDetails().getName());
        verify(categoryService, times(1)).saveCategory(any(CategoryDTO.class));
        verify(appointmentTypeRepository, times(1)).save(any(AppointmentType.class));
    }

    @Test
    void testListAll() {
        when(appointmentTypeRepository.findAll()).thenReturn(List.of(appointmentType));

        List<AppointmentType> result = appointmentTypeService.listAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Consultation", result.get(0).getAppointmentTypeDetails().getName());
    }

    @Test
    void testFindById() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(appointmentType));

        Optional<AppointmentType> result = appointmentTypeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Consultation", result.get().getAppointmentTypeDetails().getName());
    }

    @Test
    void testFindById_NotFound() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AppointmentType> result = appointmentTypeService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteByName_Success() {
        when(appointmentTypeRepository.findByName("Consultation")).thenReturn(Optional.of(appointmentType));

        appointmentTypeService.deleteByName("Consultation");

        verify(appointmentTypeRepository, times(1)).delete(any(AppointmentType.class));
    }

    @Test
    void testDeleteByName_NotFound() {
        when(appointmentTypeRepository.findByName("Consultation")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> appointmentTypeService.deleteByName("Consultation"));
    }

    @Test
    void testDeleteById_Success() {
        when(appointmentTypeRepository.existsById(1L)).thenReturn(true);

        appointmentTypeService.deleteAppointmentType(1L);

        verify(appointmentTypeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(appointmentTypeRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> appointmentTypeService.deleteAppointmentType(1L));
    }
    
    @Test
    void testFindByName() {
        when(appointmentTypeRepository.findByName("Consultation")).thenReturn(Optional.of(appointmentType));

        Optional<AppointmentType> result = appointmentTypeService.findByName("Consultation");

        assertTrue(result.isPresent());
        assertEquals("Consultation", result.get().getAppointmentTypeDetails().getName());
    }

    @Test
    void testFindByCategory() {
        when(appointmentTypeRepository.findByCategory("General")).thenReturn(List.of(appointmentType));

        List<AppointmentType> result = appointmentTypeService.findByCategory("General");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Consultation", result.get(0).getAppointmentTypeDetails().getName());
    }

    @Test
    void testFindByPriceBetween() {
        when(appointmentTypeRepository.findByPriceBetween(50.0, 150.0)).thenReturn(List.of(appointmentType));

        List<AppointmentType> result = appointmentTypeService.findByPriceBetween(50.0, 150.0);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Consultation", result.get(0).getAppointmentTypeDetails().getName());
    }

    @Test
    void testFindByPriceBetween_InvalidRange() {
        assertThrows(InvalidPriceRangeException.class, () -> appointmentTypeService.findByPriceBetween(200.0, 100.0));
    }

    @Test
    void testFindAllByOrderByEstimatedTimeAsc() {
        when(appointmentTypeRepository.findAllByOrderByEstimatedTimeAsc()).thenReturn(List.of(appointmentType));

        List<AppointmentType> result = appointmentTypeService.findAllByOrderByEstimatedTimeAsc();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(30, result.get(0).getEstimatedTime());
    }
}
