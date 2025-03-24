package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
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

    // Método para salvar a avaliação
    public Evaluation save(EvaluationDTO evaluationDTO) {
        if (evaluationDTO.getRating() < 0 || evaluationDTO.getRating() > 5) {
            throw new InvalidRatingException("Rating must be between 0 and 5");
        }

        AppointmentType appointmentType = appointmentTypeRepository.findById(evaluationDTO.getAppointmentTypeId())
                .orElseThrow(() -> new NoSuchElementException("AppointmentType not found"));

        Evaluation evaluation = new Evaluation(evaluationDTO.getRating(), evaluationDTO.getComment(), appointmentType);

        return evaluationRepository.save(evaluation);
    }

    // Método para adicionar uma avaliação
    public Evaluation addEvaluation(EvaluationDTO evaluationDTO) {
        if (evaluationDTO.getRating() < 1 || evaluationDTO.getRating() > 5) {
            throw new InvalidRatingException("A classificação deve ser entre 1 e 5.");
        }

        AppointmentType appointmentType = appointmentTypeRepository.findById(evaluationDTO.getAppointmentTypeId())
                .orElseThrow(() -> new NoSuchElementException("Tipo de agendamento não encontrado"));

        Evaluation evaluation = new Evaluation(evaluationDTO.getRating(), evaluationDTO.getComment(), appointmentType);

        return evaluationRepository.save(evaluation);
    }

    // Método para obter todas as avaliações
    public ResponseEntity<List<Evaluation>> getAllEvaluations() {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        return ResponseEntity.ok(evaluations);
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
