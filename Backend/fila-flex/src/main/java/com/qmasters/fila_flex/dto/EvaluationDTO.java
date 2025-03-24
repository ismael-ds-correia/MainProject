package com.qmasters.fila_flex.dto;

import com.qmasters.fila_flex.model.Evaluation;

public class EvaluationDTO {
    private int rating;
    private String comment;
    private Long appointmentTypeId;

    // Construtor que aceita um objeto Evaluation
    public EvaluationDTO(Evaluation evaluation) {
        this.rating = evaluation.getRating();
        this.comment = evaluation.getComment();
        this.appointmentTypeId = evaluation.getAppointmentType().getId();
    }

    // Construtor padrão
    public EvaluationDTO() {}

    // Getters and Setters
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(Long appointmentTypeId) {
        this.appointmentTypeId = appointmentTypeId;
    }
}