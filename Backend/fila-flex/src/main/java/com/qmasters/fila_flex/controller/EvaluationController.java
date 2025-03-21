package com.qmasters.fila_flex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.service.EvaluationService;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {
    @Autowired
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public EvaluationDTO createEvaluation(@RequestBody EvaluationDTO evaluationDTO) {
        Evaluation evaluation = evaluationService.addEvaluation(evaluationDTO);
        EvaluationDTO responseDTO = new EvaluationDTO();
        responseDTO.setRating(evaluation.getRating());
        responseDTO.setComment(evaluation.getComment());
        return responseDTO;
    }
    @GetMapping
    public List<EvaluationDTO> listEvaluations() {
        return evaluationService.getAllEvaluations().stream()
                .map(evaluation -> {
                    EvaluationDTO dto = new EvaluationDTO();
                    dto.setRating(evaluation.getRating());
                    dto.setComment(evaluation.getComment());
                    return dto;
                })
                .toList();
    }
}