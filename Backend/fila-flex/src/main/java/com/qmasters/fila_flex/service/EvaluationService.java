package com.qmasters.fila_flex.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.exception.InvalidRatingException;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.repository.EvaluationRepository;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;

    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    // coloquei uma condição para verificar se a avaliação está entre 0 e 5
    public Evaluation addEvaluation(EvaluationDTO evaluationDTO) {
        if (evaluationDTO.getRating() < 0 || evaluationDTO.getRating() > 5) {
            throw new InvalidRatingException("Rating must be between 0 and 5");
        }
        Evaluation evaluation = new Evaluation();
        evaluation.setRating(evaluationDTO.getRating());
        evaluation.setComment(evaluationDTO.getComment());
        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }
}