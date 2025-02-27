package com.qmasters.fila_flex.dto;

import java.time.LocalDate;
import java.util.List;

public class AppointmentTypeDTO {
    private String name;
    private String description;
    private List<String> category;
    private double price;
    private Integer runtime;
    private LocalDate estimatedTime;
    private List<String> requiredDocumentation;

    public AppointmentTypeDTO() {}

    public AppointmentTypeDTO(String name, String description, List<String> category, double price,
                              Integer runtime, LocalDate estimatedTime, List<String> requiredDocumentation) {
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

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
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

    public void setEstimatedTime(LocalDate estimatedTime) { // Corrigido aqui
        this.estimatedTime = estimatedTime;
    }

    public List<String> getRequiredDocumentation() {
        return requiredDocumentation;
    }

    public void setRequiredDocumentation(List<String> requiredDocumentation) {
        this.requiredDocumentation = requiredDocumentation;
    }
}
