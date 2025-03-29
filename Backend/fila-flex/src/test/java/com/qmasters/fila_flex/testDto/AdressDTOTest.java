package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.AdressDTO;

class AdressDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Criando um objeto AdressDTO com o construtor parametrizado
        AdressDTO address = new AdressDTO("123", "Main Street", "New York", "NY", "USA");

        // Verificações
        assertEquals("123", address.getNumber());
        assertEquals("Main Street", address.getStreet());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void testSetters() {
        // Criando um objeto AdressDTO com o construtor padrão
        AdressDTO address = new AdressDTO();

        // Definindo valores com os setters
        address.setNumber("456");
        address.setStreet("Broadway");
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setCountry("USA");

        // Verificações
        assertEquals("456", address.getNumber());
        assertEquals("Broadway", address.getStreet());
        assertEquals("Los Angeles", address.getCity());
        assertEquals("CA", address.getState());
        assertEquals("USA", address.getCountry());
    }
}
