package com.qmasters.fila_flex.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qmasters.fila_flex.util.PriorityCondition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "appointments")
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_type_id", nullable = false)
    @JsonIgnore //usado para evitar loop infinito na saida do Insomnia
    private AppointmentType appointmentType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Column(nullable = false) //mudei para "scheduled" para não ter muita repetição de "appointment"
    private LocalDateTime scheduledDateTime; //dia que ocorrera o agendamento

    private LocalDateTime createdDateTime; //talvez seja util no futuro

    @NotNull(message = "É obrigatório informar a condição de prioridade")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING) //Usando para salvar o enum como string no banco de dados, e não como um número.
    private PriorityCondition priorityCondition;

    //=================================variaveis Transients======================================
    //as variaveis transients não são obrigatorias nesta classe, mas baseado na ordem que elas estão
    //listadas aqui, elas serão exibidas desta mesma ordem no Insomnia

    @Column(nullable = false)
    private Integer queueOrder;

    //=================================Construtores======================================

    public Appointment() {
    
    }

    public Appointment(AppointmentType appointmentType, User user, LocalDateTime scheduledDateTime) {
        this.appointmentType = appointmentType;
        this.user = user;
        this.scheduledDateTime = scheduledDateTime;
        this.createdDateTime = LocalDateTime.now();//registra a hora atual
        this.queueOrder = 0; //Valor temporário, será atualizado pelo service.
        this.priorityCondition = PriorityCondition.NO_PRIORITY; //Valor padrão, para alterar usar o setter.
    }

    //================================Getters e Setters Transients================================
    //se remover estes getters, as variaveis transients não serão exibidas no Insomnia
    //por mais que nem estejam sendo chamados no DTO ou em outro lugar

    public String getAppointmentTypeName() {
        return appointmentType.getName();
    }

    public String getAppointmentTypeDescription() {
        return appointmentType.getDescription();
    }

    public List<String>getAppointmentTypeCategory() {
        return appointmentType.getCategory();
    }

    public String getAppointmentTypePrice() {
        return String.valueOf(appointmentType.getPrice());
    }

    public String getAppointmentTypeEstimatedTime() {
        return String.valueOf(appointmentType.getEstimatedTime());
    }

    public List<String> getAppointmentTypeRequiredDocumentation() {
        return appointmentType.getRequiredDocumentation();
    }

    public Adress getAppointmentTypeAdress() {
        return appointmentType.getAdress();
    }

    public String getUserEmail() {
        return user.getEmail();
    }

    public String getUserId() {
        return user.getId().toString();
    }

    //==================================Getters e Setters===================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
