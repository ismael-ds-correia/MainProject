package com.qmasters.fila_flex.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.service.EvaluationService;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {
    
    private final EvaluationService evaluationService;

    // Remover a anotação @Autowired, pois a injeção é feita via construtor
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public EvaluationDTO createEvaluation(@RequestBody EvaluationDTO evaluationDTO) {
        Evaluation evaluation = evaluationService.addEvaluation(evaluationDTO);
        EvaluationDTO responseDTO = new EvaluationDTO();
        responseDTO.setRating(evaluation.getRating());
        responseDTO.setComment(evaluation.getComment());
        responseDTO.setAppointmentTypeId(evaluation.getAppointmentType().getId());
        return responseDTO;
    }

    @GetMapping
    public List<EvaluationDTO> listEvaluations() {
        return evaluationService.getAllEvaluations().stream()
                .map(evaluation -> {
                    EvaluationDTO dto = new EvaluationDTO();
                    dto.setRating(evaluation.getRating());
                    dto.setComment(evaluation.getComment());
                    dto.setAppointmentTypeId(evaluation.getAppointmentType().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating() {
        double average = evaluationService.calculateAverageRating();
        return ResponseEntity.ok(average);
    }
}
