package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.AuthDTO;

class AuthDTOTest {

    @Test
    void testConstructorAndGetter() {
        // Criando um objeto AuthDTO com email e senha
        AuthDTO authDTO = new AuthDTO("user@example.com", "securePassword");

        // Verificando se os valores foram atribuídos corretamente
        assertEquals("user@example.com", authDTO.getEmail());
        assertEquals("securePassword", authDTO.getPassword());
    }

    @Test
    void testSetter() {
        // Criando um objeto AuthDTO sem valores iniciais
        AuthDTO authDTO = new AuthDTO();

        // Modificando valores usando os setters
        authDTO.setEmail("newuser@example.com");
        authDTO.setPassword("newSecurePassword");

        // Verificando se os valores foram atualizados corretamente
        assertEquals("newuser@example.com", authDTO.getEmail());
        assertEquals("newSecurePassword", authDTO.getPassword());
    }
}
