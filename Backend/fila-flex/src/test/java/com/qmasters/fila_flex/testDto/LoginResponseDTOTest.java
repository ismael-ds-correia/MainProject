package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.LoginResponseDTO;

class LoginResponseDTOTest {

    @Test
    void testConstructorAndGetter() {
        // Criando um objeto LoginResponseDTO com um token específico
        LoginResponseDTO response = new LoginResponseDTO("sample_token_123");

        // Verificando se o token foi atribuído corretamente
        assertEquals("sample_token_123", response.getToken());
    }

    @Test
    void testSetter() {
        // Criando um objeto LoginResponseDTO com um token inicial
        LoginResponseDTO response = new LoginResponseDTO("initial_token");

        // Modificando o token usando o setter
        response.setToken("new_token_456");

        // Verificando se o token foi atualizado corretamente
        assertEquals("new_token_456", response.getToken());
    }
}
