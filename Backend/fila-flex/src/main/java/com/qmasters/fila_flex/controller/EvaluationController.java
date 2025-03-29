package com.qmasters.fila_flex.controller;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;
    
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public EvaluationDTO createEvaluation(@RequestBody EvaluationDTO evaluationDTO) {
        return new EvaluationDTO(evaluationService.addEvaluation(evaluationDTO));
    }

    @GetMapping
    public List<EvaluationDTO> listEvaluations() {
        ResponseEntity<List<Evaluation>> response = evaluationService.getAllEvaluations();
        // Garante que o retorno sempre seja uma lista, mesmo que `getBody()` seja null
        List<Evaluation> evaluations = response.getBody() != null ? response.getBody() : List.of();

        return evaluations.stream().map(EvaluationDTO::new).toList();
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating() {
        return ResponseEntity.ok(evaluationService.calculateAverageRating());
    }
}
