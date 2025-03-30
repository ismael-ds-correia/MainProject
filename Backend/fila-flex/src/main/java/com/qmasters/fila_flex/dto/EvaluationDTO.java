package com.qmasters.fila_flex.dto;

import com.qmasters.fila_flex.model.AppointmentType;

public class EvaluationDTO {
    private int rating;
    private String comment;
    private AppointmentType appointmentType;

    
    // Construtor que aceita um objeto Evaluation
    public EvaluationDTO(int rating, String comment, AppointmentType appointmentType) {
        this.rating = rating;
        this.comment = comment;
        this.appointmentType = appointmentType;
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
    
    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

}