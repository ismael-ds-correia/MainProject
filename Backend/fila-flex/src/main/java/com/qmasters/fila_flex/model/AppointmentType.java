package com.qmasters.fila_flex.model;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointment_types")
public class AppointmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private Integer runtime; // Em minutos

    @Column(nullable = false)
    private LocalDate estimatedTime;

    @Lob
    private String requiredDocumentation;

    public AppointmentType() {}

    public AppointmentType(String name, String description, String category, double price,
                           Integer runtime, LocalDate estimatedTime, String requiredDocumentation) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.runtime = runtime;
        this.estimatedTime = estimatedTime;
        this.requiredDocumentation = requiredDocumentation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public LocalDate getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(LocalDate estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getRequiredDocumentation() {
        return requiredDocumentation;
    }

    public void setRequiredDocumentation(String requiredDocumentation) {
        this.requiredDocumentation = requiredDocumentation;
    }
}

