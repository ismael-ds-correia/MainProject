package com.qmasters.fila_flex.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.util.PriorityCondition;

public class AppointmentDTO {
    private AppointmentType appointmentType;
    private User user;
    private LocalDateTime scheduledDateTime;
    private LocalDateTime createdDateTime;
    private Integer queueOrder;
    private PriorityCondition priorityCondition;
    
    private String userId; //transient
    private String userEmail; //transient
    
    private String appointmentTypeName; //transient
    private String appointmentTypeDescription; //transient
    private List<String> appointmentTypeCategory; //transient
    private String appointmentTypePrice; //transient
    private String appointmentTypeEstimatedTime; //transient
    private List<String> appointmentTypeRequiredDocumentation; //transient
    private String appointmentTypeAdress; //transient
    private AppointmentStatus status;


    //================================Construtores===================================

    public AppointmentDTO() {
    }

    public AppointmentDTO(AppointmentType appointmentType, User user, LocalDateTime scheduledDateTime, LocalDateTime createdDateTime) {
        this.appointmentType = appointmentType;
        this.user = user;
        this.scheduledDateTime = scheduledDateTime;
        this.createdDateTime = createdDateTime;

        this.userId = user.getId().toString(); //transient
        this.userEmail = user.getEmail(); //transient

        this.appointmentTypeName = appointmentType.getName(); //transient
        this.appointmentTypeDescription = appointmentType.getDescription(); //transient
        this.appointmentTypeCategory = appointmentType.getCategory(); //transient
        this.appointmentTypePrice = String.valueOf(appointmentType.getPrice()); //transient
        this.appointmentTypeEstimatedTime = String.valueOf(appointmentType.getEstimatedTime()); //transient
        this.appointmentTypeRequiredDocumentation = appointmentType.getRequiredDocumentation(); //transient
        this.appointmentTypeAdress = appointmentType.getAdressAsString(); //transient
        this.status = AppointmentStatus.MARKED; // Status padrão ao criar
    }

    //Construtor para incluir queueOrder.
    public AppointmentDTO(AppointmentType appointmentType, User user, LocalDateTime scheduledDateTime, 
                         LocalDateTime createdDateTime, Integer queueOrder) {
        this.appointmentType = appointmentType;
        this.user = user;
        this.scheduledDateTime = scheduledDateTime;
        this.createdDateTime = createdDateTime;
        this.queueOrder = queueOrder;

        this.userId = user.getId().toString();
        this.userEmail = user.getEmail();

        this.appointmentTypeName = appointmentType.getName();
        this.appointmentTypeDescription = appointmentType.getDescription();
        this.appointmentTypeCategory = appointmentType.getCategory();
        this.appointmentTypePrice = String.valueOf(appointmentType.getPrice());
        this.appointmentTypeEstimatedTime = String.valueOf(appointmentType.getEstimatedTime());
        this.appointmentTypeRequiredDocumentation = appointmentType.getRequiredDocumentation();
        this.appointmentTypeAdress = appointmentType.getAdressAsString();
        this.status = AppointmentStatus.MARKED;
    }

    //Mais uma sobrecarga de construtor para incluir priorityCondition.
    public AppointmentDTO(AppointmentType appointmentType, User user, LocalDateTime scheduledDateTime, 
                         LocalDateTime createdDateTime, Integer queueOrder, PriorityCondition priorityCondition) {
        this.appointmentType = appointmentType;
        this.user = user;
        this.scheduledDateTime = scheduledDateTime;
        this.createdDateTime = createdDateTime;
        this.queueOrder = queueOrder;
        this.priorityCondition = priorityCondition;

        this.userId = user.getId().toString();
        this.userEmail = user.getEmail();

        this.appointmentTypeName = appointmentType.getName();
        this.appointmentTypeDescription = appointmentType.getDescription();
        this.appointmentTypeCategory = appointmentType.getCategory();
        this.appointmentTypePrice = String.valueOf(appointmentType.getPrice());
        this.appointmentTypeEstimatedTime = String.valueOf(appointmentType.getEstimatedTime());
        this.appointmentTypeRequiredDocumentation = appointmentType.getRequiredDocumentation();
        this.appointmentTypeAdress = appointmentType.getAdressAsString();
        this.status = AppointmentStatus.MARKED;
    }

    //=================================Getter e Setters transients========================================
    //aparentemente, os getters e setters transients do DTO nao sao necessarios, pois mesmo apagando eles
    //a saida no Insomnia fica correta, mas melhor manter eles aqui por precaucao.
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

    public Integer getQueueOrder() {
        return queueOrder;
    }

    public void setQueueOrder(Integer queueOrder) {
        this.queueOrder = queueOrder;
    }

    public PriorityCondition getPriorityCondition() {
        return priorityCondition;
    }

    public void setPriorityCondition(PriorityCondition priorityCondition) {
        this.priorityCondition = priorityCondition;
    }
    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status; // Para alterar dinamicamente conforme a fila
    }
}