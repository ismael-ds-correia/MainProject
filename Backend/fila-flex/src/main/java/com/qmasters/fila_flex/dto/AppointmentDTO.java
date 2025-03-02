package com.qmasters.fila_flex.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.User;

public class AppointmentDTO {
    private AppointmentType appointmentType;
    private User user;
    private LocalDateTime scheduledDateTime;
    private LocalDateTime createdDateTime;
    
    private String appointmentTypeName; //transient
    private String appointmentTypeDescription; //transient
    private List<String> appointmentTypeCategory; //transient
    private String appointmentTypePrice; //transient
    private String appointmentTypeEstimatedTime; //transient
    private List<String> appointmentTypeRequiredDocumentation; //transient
    private String appointmentTypeAdress; //transient

    private String userEmail; //transient
    private String userId; //transient

    //================================Construtores===================================

    public AppointmentDTO() {
    }

    public AppointmentDTO(AppointmentType appointmentType, User user, LocalDateTime scheduledDateTime, LocalDateTime createdDateTime) {
        this.appointmentType = appointmentType;
        this.user = user;
        this.scheduledDateTime = scheduledDateTime;
        this.createdDateTime = createdDateTime;

        this.appointmentTypeName = appointmentType.getName(); //transient
        this.appointmentTypeDescription = appointmentType.getDescription(); //transient
        this.appointmentTypeCategory = appointmentType.getCategory(); //transient
        this.appointmentTypePrice = String.valueOf(appointmentType.getPrice()); //transient
        this.appointmentTypeEstimatedTime = String.valueOf(appointmentType.getEstimatedTime()); //transient
        this.appointmentTypeRequiredDocumentation = appointmentType.getRequiredDocumentation(); //transient
        this.appointmentTypeAdress = appointmentType.getAdressAsString(); //transient
        
        this.userEmail = user.getEmail(); //transient
        this.userId = user.getId().toString(); //transient
    }

    //=================================Getter e Setters transients========================================
    
    public String getAppointmentTypeName() {
        return appointmentTypeName;
    }
    
    public void setAppointmentTypeName(String appointmentTypeName) {
        this.appointmentTypeName = appointmentTypeName;
    }
    
    public String getAppointmentTypeDescription() {
        return appointmentTypeDescription;
    }
    
    public void setAppointmentTypeDescription(String appointmentTypeDescription) {
        this.appointmentTypeDescription = appointmentTypeDescription;
    }
    
    public List<String> getAppointmentTypeCategory() {
        return appointmentTypeCategory;
    }

    public void setAppointmentTypeCategory(List<String> appointmentTypeCategory) {
        this.appointmentTypeCategory = appointmentTypeCategory;
    }
    
    public String getAppointmentTypePrice() {
        return appointmentTypePrice;
    }
    
    public void setAppointmentTypePrice(String appointmentTypePrice) {
        this.appointmentTypePrice = appointmentTypePrice;
    }
    
    public String getAppointmentTypeEstimatedTime() {
        return appointmentTypeEstimatedTime;
    }
    
    public void setAppointmentTypeEstimatedTime(String appointmentTypeEstimatedTime) {
        this.appointmentTypeEstimatedTime = appointmentTypeEstimatedTime;
    }
    
    public List<String> getAppointmentTypeRequiredDocumentation() {
        return appointmentTypeRequiredDocumentation;
    }
    
    public void setAppointmentTypeRequiredDocumentation(List<String> appointmentTypeRequiredDocumentation) {
        this.appointmentTypeRequiredDocumentation = appointmentTypeRequiredDocumentation;
    }

    public String getAppointmentTypeAdress() {
        return appointmentTypeAdress;
    }

    public void setAppointmentTypeAdress(String appointmentTypeAdress) {
        this.appointmentTypeAdress = appointmentTypeAdress;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    //==============================Getters e Setters=================================

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}