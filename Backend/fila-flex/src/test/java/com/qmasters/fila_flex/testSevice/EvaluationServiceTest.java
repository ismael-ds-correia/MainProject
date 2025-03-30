package com.qmasters.fila_flex.testSevice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.exception.InvalidRatingException;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.repository.EvaluationRepository;
import com.qmasters.fila_flex.service.EvaluationService;

class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveEvaluation_Valid() {
        // Cria um AppointmentType e um DTO contendo-o
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        EvaluationDTO dto = new EvaluationDTO(5, "Excellent service", appointmentType);

        when(evaluationRepository.save(any(Evaluation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Evaluation savedEvaluation = evaluationService.saveEvaluation(dto);

        assertNotNull(savedEvaluation);
        assertEquals(5, savedEvaluation.getRating());
        assertEquals("Excellent service", savedEvaluation.getComment());
        assertEquals(appointmentType, savedEvaluation.getAppointmentType());
    }

    @Test
    void testSaveEvaluation_InvalidRatingAboveFive() {
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        EvaluationDTO dto = new EvaluationDTO(6, "Invalid rating", appointmentType);

        assertThrows(InvalidRatingException.class, () -> evaluationService.saveEvaluation(dto));
    }

    @Test
    void testSaveEvaluation_InvalidRatingBelowZero() {
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        EvaluationDTO dto = new EvaluationDTO(-1, "Invalid rating", appointmentType);

        assertThrows(InvalidRatingException.class, () -> evaluationService.saveEvaluation(dto));
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

        List<Evaluation> evaluations = evaluationService.getAllEvaluations();
        assertNotNull(evaluations);
        assertEquals(2, evaluations.size());
    }

    @Test
    void testCalculateAverageRating_WithEvaluations() {
        Evaluation eval1 = new Evaluation();
        eval1.setRating(4);

        Evaluation eval2 = new Evaluation();
        eval2.setRating(2);

        when(evaluationRepository.findAll()).thenReturn(List.of(eval1, eval2));

        double average = evaluationService.calculateAverageRating();
        assertEquals(3.0, average);
    }

    @Test
    void testCalculateAverageRating_NoEvaluations() {
        when(evaluationRepository.findAll()).thenReturn(List.of());
        double average = evaluationService.calculateAverageRating();
        assertEquals(0.0, average);
    }

    @Test
    void testDeleteEvaluation_Success() {
        Long id = 1L;
        when(evaluationRepository.existsById(id)).thenReturn(true);

        evaluationService.deleteEvaluation(id);

        verify(evaluationRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteEvaluation_NotFound() {
        Long id = 1L;
        when(evaluationRepository.existsById(id)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> evaluationService.deleteEvaluation(id));
    }
}