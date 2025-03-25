package com.qmasters.fila_flex.dto;

import java.time.LocalDate;
import java.util.List;

import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;

public class AppointmentTypeDTO {
    private AppointmentTypeDetails appointmentTypeDetails;
    private Integer estimatedTime;
    private Adress adress;

    public AppointmentTypeDTO() {}
    
    public AppointmentTypeDTO(AppointmentTypeDetails appointmentTypeDetails, Integer estimatedTime, Adress adress) {
        this.appointmentTypeDetails = appointmentTypeDetails;
        this.estimatedTime = estimatedTime;
        this.adress = adress;
    }

    public AppointmentTypeDetails getAppointmentTypeDetails() {
        return appointmentTypeDetails;
    }
    
    public void setAppointmentTypeDetails(AppointmentTypeDetails appointmentTypeDetails) {
        this.appointmentTypeDetails = appointmentTypeDetails;
    }
    
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    
    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }

    public void setName(String name) {
        appointmentTypeDetails.setName(name);
    }

    public String getName() {
        return appointmentTypeDetails.getName();
    }

    public void setDescription(String description) {
        appointmentTypeDetails.setDescription(description);
    }

    public String getDescription() {
        return appointmentTypeDetails.getDescription();
    }

    public void setCategory(String category) {
        appointmentTypeDetails.setCategory(category);
    }

    public List<String> getCategory() {
        return appointmentTypeDetails.getCategory();
    }

    public void setCategory(List<String> category) {
        appointmentTypeDetails.setCategory(category);
    }

    public void setPrice(Double price) {
        appointmentTypeDetails.setPrice(price);
    }

    public Double getPrice() {
        return appointmentTypeDetails.getPrice();
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        appointmentTypeDetails.setAppointmentDate(appointmentDate);
    }

    public LocalDate getAppointmentDate() {
        return appointmentTypeDetails.getAppointmentDate();
    }

    public void setRequiredDocumentation(List<String> requiredDocumentation) {
        appointmentTypeDetails.setRequiredDocumentation(requiredDocumentation);
    }

    public List<String> getRequiredDocumentation() {
        return appointmentTypeDetails.getRequiredDocumentation();
    }

    
}
