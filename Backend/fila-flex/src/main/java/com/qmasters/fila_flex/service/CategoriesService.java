package com.qmasters.fila_flex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.CategoriesDTO;
import com.qmasters.fila_flex.model.Categories;
import com.qmasters.fila_flex.repository.CategoriesRepository;

@Service
public class CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public CategoriesDTO addCategory(String categoryName) {
        // Valida o nome da categoria
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria não pode ser vazio.");
        }
        
        //Recupera a entidade Categories existente (ou cria uma nova, se não existir).
        Categories categories = categoriesRepository.findById(1L).orElse(new Categories());

        //Verifica se a categoria já existe.
        if (categories.getCategoriesNames().contains(categoryName)) {
            throw new IllegalArgumentException("Categoria já existe: " + categoryName);
        }

        //Adiciona a nova categoria à lista.
        categories.getCategoriesNames().add(categoryName);  

        //Salva a entidade atualizada no banco de dados.
        categories = this.categoriesRepository.save(categories);

        //Converte a entidade para DTO e retorna.
        return toDTO(categories);
    }

    //Método auxiliar para converter entidade para DTO.
    private CategoriesDTO toDTO(Categories categories) {
        CategoriesDTO dto = new CategoriesDTO();
        dto.setCategoriesNames(categories.getCategoriesNames());

        return dto;
    }

    public CategoriesDTO getAllCategories() {
        //Recupera a entidade Categories existente (ou cria uma nova, se não existir).
        Categories categories = categoriesRepository.findById(1L).orElse(new Categories());

        //Converte a entidade para DTO e retorna.
        return toDTO(categories);
    }
}
