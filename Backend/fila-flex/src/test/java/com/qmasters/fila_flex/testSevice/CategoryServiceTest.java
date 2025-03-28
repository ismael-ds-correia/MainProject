package com.qmasters.fila_flex.testSevice;

import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.model.Category;
import com.qmasters.fila_flex.repository.CategoryRepository;
import com.qmasters.fila_flex.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@ExtendWith(MockitoExtension.class) // Usando a extensão do Mockito com JUnit 5
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        // Preparando dados para o teste
        categoryDTO = new CategoryDTO("Test Category");
    }

    @Test
    void testSaveCategory_whenCategoryDoesNotExist() {
        // Cenário: A categoria não existe e precisa ser salva
        when(categoryRepository.findByName(categoryDTO.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category(categoryDTO.getName()));

        Category savedCategory = categoryService.saveCategory(categoryDTO);

        assertNotNull(savedCategory);
        assertEquals(categoryDTO.getName(), savedCategory.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testSaveCategory_whenCategoryAlreadyExists() {
        // Cenário: A categoria já existe, então não é necessário salvar novamente
        Category existingCategory = new Category(categoryDTO.getName());
        when(categoryRepository.findByName(categoryDTO.getName())).thenReturn(Optional.of(existingCategory));

        Category savedCategory = categoryService.saveCategory(categoryDTO);

        assertNotNull(savedCategory);
        assertEquals(categoryDTO.getName(), savedCategory.getName());
        verify(categoryRepository, times(0)).save(any(Category.class));  // Não deve chamar save()
    }

    @Test
    void testGetAllCategories() {
        // Cenário: Obter todas as categorias
        when(categoryRepository.findAll()).thenReturn(List.of(new Category("Category 1"), new Category("Category 2")));

        List<Category> categories = categoryService.getAllCategories();

        assertNotNull(categories);
        assertEquals(2, categories.size());
    }

    @Test
    void testGetCategoryById() {
        // Cenário: Buscar uma categoria por ID existente
        Category category = new Category("Category 1");
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category foundCategory = categoryService.getCategoryById(1L);

        assertNotNull(foundCategory);
        assertEquals("Category 1", foundCategory.getName());
    }

    @Test
    void testGetCategoryById_notFound() {
        // Cenário: Buscar uma categoria por ID que não existe
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(1L));

        assertEquals("Categoria não encontrada com o ID: 1", exception.getMessage());
    }

    @Test
    void testUpdateCategory() {
        // Cenário: Atualizar uma categoria existente
        Category existingCategory = new Category("Old Category");
        existingCategory.setId(1L);
        CategoryDTO updatedCategoryDTO = new CategoryDTO("Updated Category");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category(updatedCategoryDTO.getName()));

        Category updatedCategory = categoryService.updateCategory(1L, updatedCategoryDTO);

        assertNotNull(updatedCategory);
        assertEquals(updatedCategoryDTO.getName(), updatedCategory.getName());
    }

    @Test
    void testDeleteCategory() {
        // Cenário: Excluir uma categoria existente
        Category category = new Category("Category to be deleted");
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).delete(category);
    }
}
