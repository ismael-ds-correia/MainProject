package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.exception.InvalidRatingException;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.repository.EvaluationRepository;

import jakarta.transaction.Transactional;

@Service
public class EvaluationService {
    
    private final EvaluationRepository evaluationRepository;

    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    @Transactional
    public Evaluation saveEvaluation(EvaluationDTO evaluationDTO) {
        if (evaluationDTO.getRating() < 0 || evaluationDTO.getRating() > 5) {
            throw new InvalidRatingException("Avaliação precisa ser entre 0 e 5");
        }

        Evaluation evaluation = new Evaluation(
            evaluationDTO.getRating(), 
            evaluationDTO.getComment(), 
            evaluationDTO.getAppointmentType()
        );            

        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Optional<Evaluation> findEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }

    // Método que calcula a média das avaliações
    public double calculateAverageRating() {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        return evaluations.stream()
                .mapToInt(Evaluation::getRating)
                .average()
                .orElse(0.0);
    }

    @Transactional
    public void deleteEvaluation(Long id) {
        if (evaluationRepository.existsById(id)) {
            evaluationRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Avaliação não encontrada, remoção não foi realizada");
        }
    }
}
