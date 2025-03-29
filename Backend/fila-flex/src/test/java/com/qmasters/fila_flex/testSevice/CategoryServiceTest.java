package com.qmasters.fila_flex.testSevice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.model.Category;
import com.qmasters.fila_flex.repository.CategoryRepository;
import com.qmasters.fila_flex.service.CategoryService;

import java.util.List;
import java.util.Optional;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepositoryMock);
    }

    @Test
    void testSaveCategory_WhenCategoryExists() {
        // Dado
        String categoryName = "Technology";
        CategoryDTO categoryDTO = new CategoryDTO(categoryName);
        Category existingCategory = new Category(categoryName);

        // Simulando o comportamento do repositório (categoria já existe)
        when(categoryRepositoryMock.findByName(categoryName)).thenReturn(Optional.of(existingCategory));

        // Chamando o método
        Category result = categoryService.saveCategory(categoryDTO);

        // Verificando o comportamento esperado
        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        verify(categoryRepositoryMock, times(1)).findByName(categoryName);
        verify(categoryRepositoryMock, times(0)).save(any(Category.class));  // Verificando se o save não foi chamado
    }

    @Test
    void testSaveCategory_WhenCategoryDoesNotExist() {
        // Dado
        String categoryName = "Health";
        CategoryDTO categoryDTO = new CategoryDTO(categoryName);

        // Simulando o comportamento do repositório (categoria não existe)
        when(categoryRepositoryMock.findByName(categoryName)).thenReturn(Optional.empty());
        when(categoryRepositoryMock.save(any(Category.class))).thenReturn(new Category(categoryName));

        // Chamando o método
        Category result = categoryService.saveCategory(categoryDTO);

        // Verificando o comportamento esperado
        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        verify(categoryRepositoryMock, times(1)).findByName(categoryName);
        verify(categoryRepositoryMock, times(1)).save(any(Category.class));  // Verificando se o save foi chamado
    }

    @Test
    void testGetAllCategories() {
        // Dado
        Category category1 = new Category("Technology");
        Category category2 = new Category("Health");
        List<Category> categories = List.of(category1, category2);

        // Simulando o comportamento do repositório
        when(categoryRepositoryMock.findAll()).thenReturn(categories);

        // Chamando o método
        List<Category> result = categoryService.getAllCategories();

        // Verificando o comportamento esperado
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(category1));
        assertTrue(result.contains(category2));
        verify(categoryRepositoryMock, times(1)).findAll();
    }

    @Test
void testGetCategoryById_Found() {
    // Dado
    Long categoryId = 1L;
    Category category = new Category("Technology");
    category.setId(categoryId); // Definindo explicitamente o ID

    // Simulando o comportamento do repositório
    when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category));

    // Chamando o método
    Category result = categoryService.getCategoryById(categoryId);

    // Verificando o comportamento esperado
    assertNotNull(result);
    assertEquals(categoryId, result.getId());
    assertEquals("Technology", result.getName());
    verify(categoryRepositoryMock, times(1)).findById(categoryId);
}

    @Test
    void testGetCategoryById_NotFound() {
        // Dado
        Long categoryId = 1L;

        // Simulando o comportamento do repositório
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty());

        // Chamando o método
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });

        // Verificando a mensagem da exceção
        assertEquals("Categoria não encontrada com o ID: " + categoryId, thrown.getMessage());
        verify(categoryRepositoryMock, times(1)).findById(categoryId);
    }

    @Test
    void testUpdateCategory() {
        // Dado
        Long categoryId = 1L;
        CategoryDTO categoryDTO = new CategoryDTO("New Category Name");
        Category category = new Category("Old Category Name");
        
        // Simulando o comportamento do repositório
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepositoryMock.save(any(Category.class))).thenReturn(category);

        // Chamando o método
        Category result = categoryService.updateCategory(categoryId, categoryDTO);

        // Verificando o comportamento esperado
        assertNotNull(result);
        assertEquals("New Category Name", result.getName());
        verify(categoryRepositoryMock, times(1)).findById(categoryId);
        verify(categoryRepositoryMock, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        // Dado
        Long categoryId = 1L;
        Category category = new Category("Technology");

        // Simulando o comportamento do repositório
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category));

        // Chamando o método
        categoryService.deleteCategory(categoryId);

        // Verificando o comportamento esperado
        verify(categoryRepositoryMock, times(1)).delete(category);
    }

    @Test
    void testDeleteCategory_NotFound() {
        // Dado
        Long categoryId = 1L;

        // Simulando o comportamento do repositório
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty());

        // Chamando o método
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        // Verificando a mensagem da exceção
        assertEquals("Categoria não encontrada com o ID: " + categoryId, thrown.getMessage());
        verify(categoryRepositoryMock, times(1)).findById(categoryId);
    }
}
