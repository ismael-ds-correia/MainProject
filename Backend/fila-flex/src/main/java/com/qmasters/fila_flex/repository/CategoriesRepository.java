package com.qmasters.fila_flex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    // Método para verificar se uma categoria já existe.
    boolean existsByCategoriesNamesContaining(String categoryName);
}