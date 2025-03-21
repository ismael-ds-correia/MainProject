package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.model.Category;
import com.qmasters.fila_flex.repository.CategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category saveCategory(CategoryDTO categoryDTO) {
        //Verifica se a categoria já existe.
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryDTO.getName());
        if (optionalCategory.isPresent()) {
            return optionalCategory.get(); //Retorna a categoria existente.
        }

        //Cria e salva uma nova categoria.
        Category category = new Category(categoryDTO.getName());
        return categoryRepository.save(category);
    }

    //Retorna todas as categorias.
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    //Busca uma categoria pelo ID.
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + id));
    }

    //Atualiza uma categoria.
    @Transactional
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = getCategoryById(id); //Busca a categoria existente.
        category.setName(categoryDTO.getName()); //Atualiza o nome.
        return categoryRepository.save(category); //Salva a categoria atualizada.
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id); // Busca a categoria existente
        categoryRepository.delete(category); // Exclui a categoria
    }
}