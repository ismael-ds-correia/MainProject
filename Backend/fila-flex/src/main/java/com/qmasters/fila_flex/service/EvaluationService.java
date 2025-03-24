package com.qmasters.fila_flex.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.exception.InvalidRatingException;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.repository.EvaluationRepository;

@Service
public class EvaluationService {
    
    private final EvaluationRepository evaluationRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;

    public EvaluationService(EvaluationRepository evaluationRepository, AppointmentTypeRepository appointmentTypeRepository) {
        this.evaluationRepository = evaluationRepository;
        this.appointmentTypeRepository = appointmentTypeRepository;
    }

    public Evaluation addEvaluation(EvaluationDTO evaluationDTO) {
        if (evaluationDTO.getRating() < 0 || evaluationDTO.getRating() > 5) {
            throw new InvalidRatingException("Rating must be between 0 and 5");
        }

        AppointmentType appointmentType = appointmentTypeRepository.findById(evaluationDTO.getAppointmentTypeId())
                .orElseThrow(() -> new RuntimeException("AppointmentType not found"));

        Evaluation evaluation = new Evaluation();
        evaluation.setRating(evaluationDTO.getRating());
        evaluation.setComment(evaluationDTO.getComment());
        evaluation.setAppointmentType(appointmentType);

        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    // Método que calcula a média das avaliações
    public double calculateAverageRating() {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        return evaluations.stream()
                .mapToInt(Evaluation::getRating)
                .average()
                .orElse(0.0);
    }
}
