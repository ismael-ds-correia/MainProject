package com.qmasters.fila_flex.controller;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.service.EvaluationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;
    
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/create")
    public ResponseEntity<Evaluation> createEvaluation(@RequestBody EvaluationDTO evaluationDTO) {
        var evaluation = evaluationService.saveEvaluation(evaluationDTO);
        return ResponseEntity.ok(evaluation);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Evaluation>> getAllEvaluations() {
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @GetMapping("/find-id/{id}")
    public ResponseEntity<Evaluation> getEvaluationById(@PathVariable Long id) {
        var evaluation = evaluationService.findEvaluationById(id);
        if (evaluation.isEmpty()) {
            throw new NoSuchElementException("Avaliação não encontrada");
        }
        return ResponseEntity.ok(evaluation.get());
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating() {
        return ResponseEntity.ok(evaluationService.calculateAverageRating());
    }

    @DeleteMapping("/delete-id/{id}")
    public ResponseEntity<String> deleteEvaluation(@PathVariable Long id) {
        try {
            evaluationService.deleteEvaluation(id);
            return ResponseEntity.ok("Avaliação removida com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
