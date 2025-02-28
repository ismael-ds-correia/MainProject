package com.qmasters.fila_flex.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDTO {
    private List<String> categoriesNames;

    public CategoriesDTO(Long id, List<String> categoriesNames) {
        this.categoriesNames = categoriesNames;
    }

    public CategoriesDTO() {
        this.categoriesNames = new ArrayList<>();
    }

    public List<String> getCategoriesNames() {
        return categoriesNames;
    }

    public void setCategoriesNames(List<String> categoriesNames) {
        this.categoriesNames = categoriesNames;
    }   
}
