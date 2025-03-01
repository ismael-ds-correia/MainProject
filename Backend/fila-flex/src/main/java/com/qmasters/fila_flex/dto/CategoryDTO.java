package com.qmasters.fila_flex.dto;

public class CategoryDTO {
    private String name;

    public CategoryDTO() {}

    public CategoryDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
