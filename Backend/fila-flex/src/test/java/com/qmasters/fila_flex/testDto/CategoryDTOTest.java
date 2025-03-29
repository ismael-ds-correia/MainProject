package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.CategoryDTO;

class CategoryDTOTest {

    @Test
    void testConstructorAndGetter() {
        // Criando um objeto CategoryDTO com um nome específico
        CategoryDTO category = new CategoryDTO("Health");

        // Verificando se o nome foi atribuído corretamente
        assertEquals("Health", category.getName());
    }

    @Test
    void testSetter() {
        // Criando um objeto CategoryDTO sem nome
        CategoryDTO category = new CategoryDTO();

        // Modificando o nome usando o setter
        category.setName("Education");

        // Verificando se o nome foi atualizado corretamente
        assertEquals("Education", category.getName());
    }
}

