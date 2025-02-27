package com.qmasters.fila_flex.dto;

import java.time.LocalDate;

public class AppointmentTypeDTO {
    private String name;
    private String description;
    private String category;
    private double price;
    private Integer runtime;
    private LocalDate estimatedTime;
    private String requiredDocumentation;

    public AppointmentTypeDTO() {}

    public AppointmentTypeDTO(String name, String description, String category, double price,
                              Integer runtime, LocalDate estimatedTime, String requiredDocumentation) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.runtime = runtime;
        this.estimatedTime = estimatedTime;
        this.requiredDocumentation = requiredDocumentation;
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
