package com.qmasters.fila_flex.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.service.EvaluationService;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public ResponseEntity<EvaluationDTO> createEvaluation(@RequestBody EvaluationDTO evaluationDTO) {
        return evaluationService.addEvaluation(evaluationDTO);
    }

    @GetMapping
    public ResponseEntity<List<EvaluationDTO>> listEvaluations() {
        return evaluationService.getAllEvaluations();
    }
}