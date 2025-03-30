package com.qmasters.fila_flex.testController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.controller.EvaluationController;
import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.service.EvaluationService;

class EvaluationControllerTest {

    @Mock
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController evaluationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEvaluation() {
        // Preparar dados: criar AppointmentType e EvaluationDTO via construtor atualizado
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        EvaluationDTO dto = new EvaluationDTO(4, "Great service", appointmentType);

        // Cria uma Evaluation para simular o retorno do service
        Evaluation evaluation = new Evaluation();
        evaluation.setRating(4);
        evaluation.setComment("Great service");
        evaluation.setAppointmentType(appointmentType);

        when(evaluationService.saveEvaluation(dto)).thenReturn(evaluation);

        ResponseEntity<Evaluation> response = evaluationController.createEvaluation(dto);

        assertEquals(200, response.getStatusCode().value());
        Evaluation body = response.getBody();
        assertNotNull(body);
        assertEquals(4, body.getRating());
        assertEquals("Great service", body.getComment());
        assertEquals(appointmentType, body.getAppointmentType());
    }

    @Test
    void testGetAllEvaluations() {
        Evaluation eval1 = new Evaluation();
        eval1.setRating(4);
        eval1.setComment("Good");
        
        Evaluation eval2 = new Evaluation();
        eval2.setRating(5);
        eval2.setComment("Excellent");

        List<Evaluation> evaluations = List.of(eval1, eval2);
        when(evaluationService.getAllEvaluations()).thenReturn(evaluations);

        ResponseEntity<List<Evaluation>> response = evaluationController.getAllEvaluations();

        assertEquals(200, response.getStatusCode().value());
        List<Evaluation> responseList = response.getBody();
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
    }

    @Test
    void testGetEvaluationById_Found() {
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        
        Evaluation evaluation = new Evaluation();
        evaluation.setRating(4);
        evaluation.setComment("Good service");
        evaluation.setAppointmentType(appointmentType);
        Long id = 1L;
        
        when(evaluationService.findEvaluationById(id)).thenReturn(Optional.of(evaluation));

        ResponseEntity<Evaluation> response = evaluationController.getEvaluationById(id);

        assertEquals(200, response.getStatusCode().value());
        Evaluation body = response.getBody();
        assertNotNull(body);
        assertEquals(4, body.getRating());
        assertEquals("Good service", body.getComment());
        assertEquals(appointmentType, body.getAppointmentType());
    }

    @Test
    void testGetEvaluationById_NotFound() {
        Long id = 1L;
        when(evaluationService.findEvaluationById(id)).thenReturn(Optional.empty());
        
        assertThrows(NoSuchElementException.class, () -> evaluationController.getEvaluationById(id));
    }

    @Test
    void testGetAverageRating() {
        when(evaluationService.calculateAverageRating()).thenReturn(4.0);

        ResponseEntity<Double> response = evaluationController.getAverageRating();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(4.0, response.getBody());
    }

    @Test
    void testDeleteEvaluation_Success() {
        Long id = 1L;
        doNothing().when(evaluationService).deleteEvaluation(id);

        ResponseEntity<String> response = evaluationController.deleteEvaluation(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Avaliação removida com sucesso", response.getBody());
    }

    @Test
    void testDeleteEvaluation_Failure() {
        Long id = 1L;
        doThrow(new IllegalArgumentException("Avaliação não encontrada")).when(evaluationService).deleteEvaluation(id);

        ResponseEntity<String> response = evaluationController.deleteEvaluation(id);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Avaliação não encontrada", response.getBody());
    }
}