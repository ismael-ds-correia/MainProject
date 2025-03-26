package com.qmasters.fila_flex.testSevice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.exception.InvalidRatingException;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.repository.EvaluationRepository;
import com.qmasters.fila_flex.service.EvaluationService;

class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @InjectMocks
    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEvaluation_Valid() {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setRating(4);
        dto.setComment("Great service");
        dto.setAppointmentTypeId(1L);
        
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(appointmentType));
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Evaluation savedEvaluation = evaluationService.addEvaluation(dto);
        
        assertNotNull(savedEvaluation);
        assertEquals(4, savedEvaluation.getRating());
        assertEquals("Great service", savedEvaluation.getComment());
        assertEquals(1L, savedEvaluation.getAppointmentType().getId());
    }

    @Test
    void testAddEvaluation_InvalidRating() {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setRating(6);  // Classificação inválida
        
        assertThrows(InvalidRatingException.class, () -> evaluationService.addEvaluation(dto));
    }

    @Test
    void testAddEvaluation_AppointmentTypeNotFound() {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setRating(3);
        dto.setAppointmentTypeId(99L);
        
        when(appointmentTypeRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> evaluationService.addEvaluation(dto));
    }

    @Test
    void testGetAllEvaluations() {
        Evaluation eval1 = new Evaluation();
        eval1.setRating(5);
        eval1.setComment("Excellent");
        
        Evaluation eval2 = new Evaluation();
        eval2.setRating(3);
        eval2.setComment("Average");
        
        when(evaluationRepository.findAll()).thenReturn(List.of(eval1, eval2));
        
        List<Evaluation> evaluations = evaluationService.getAllEvaluations().getBody();
        
        assertNotNull(evaluations);
        assertEquals(2, evaluations.size());
        assertEquals("Excellent", evaluations.get(0).getComment());
    }
}
