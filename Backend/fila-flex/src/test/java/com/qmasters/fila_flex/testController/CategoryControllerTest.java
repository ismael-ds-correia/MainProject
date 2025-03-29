package com.qmasters.fila_flex.testController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.model.Category;
import com.qmasters.fila_flex.service.CategoryService;
import com.qmasters.fila_flex.controller.CategoryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        // Setup de uma categoria para usar nos testes
        category = new Category("Electronics");
        category.setId(1L);

        categoryDTO = new CategoryDTO("Electronics");
    }

    @Test
    void testCreateCategory() throws Exception {
        // Simula o comportamento do service
        when(categoryService.saveCategory(any(CategoryDTO.class))).thenReturn(category);

        // Envia o POST request para criar uma categoria
        mockMvc.perform(post("/category/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated()) // Espera status 201 (Created)
                .andExpect(jsonPath("$.id").value(1)) // Verifica se o ID retornado é 1
                .andExpect(jsonPath("$.name").value("Electronics")); // Verifica se o nome é "Electronics"
    }

    @Test
    void testGetAllCategories() throws Exception {
        // Simula o comportamento do service
        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        // Envia o GET request para pegar todas as categorias
        mockMvc.perform(get("/category/all"))
                .andExpect(status().isOk()) // Espera status 200 (OK)
                .andExpect(jsonPath("$[0].id").value(1)) // Verifica se o ID da categoria é 1
                .andExpect(jsonPath("$[0].name").value("Electronics")); // Verifica se o nome da categoria é "Electronics"
    }

    @Test
    void testGetCategoryById() throws Exception {
        // Simula o comportamento do service
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        // Envia o GET request para pegar a categoria com ID 1
        mockMvc.perform(get("/category/find-id/{id}", 1L))
                .andExpect(status().isOk()) // Espera status 200 (OK)
                .andExpect(jsonPath("$.id").value(1)) // Verifica se o ID retornado é 1
                .andExpect(jsonPath("$.name").value("Electronics")); // Verifica se o nome é "Electronics"
    }

    @Test
    void testUpdateCategory() throws Exception {
        // Simula o comportamento do service
        when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class))).thenReturn(category);

        // Envia o PUT request para atualizar a categoria com ID 1
        mockMvc.perform(put("/category/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isOk()) // Espera status 200 (OK)
                .andExpect(jsonPath("$.id").value(1)) // Verifica se o ID retornado é 1
                .andExpect(jsonPath("$.name").value("Electronics")); // Verifica se o nome é "Electronics"
    }

    @Test
    void testDeleteCategory() throws Exception {
        // Simula o comportamento do service
        doNothing().when(categoryService).deleteCategory(1L);

        // Envia o DELETE request para deletar a categoria com ID 1
        mockMvc.perform(delete("/category/delete-id/{id}", 1L))
                .andExpect(status().isNoContent()); // Espera status 204 (No Content)
    }
}
