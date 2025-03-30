package com.qmasters.fila_flex.testController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.controller.AdressController;
import com.qmasters.fila_flex.dto.AdressDTO;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.service.AdressService;

@ExtendWith(MockitoExtension.class)
public class AdressControllerTest {

    @Mock
    private AdressService adressService;

    @InjectMocks
    private AdressController adressController;

    private Adress adress;
    private AdressDTO adressDTO;

    @BeforeEach
    void setUp() {
        // Inicializa um endereço válido para os testes
        adress = new Adress("123", "Main Street", "CityX", "StateY", "CountryZ");
        // Inicializa um DTO com os mesmos valores (ajuste conforme sua implementação)
        adressDTO = new AdressDTO("123", "Main Street", "CityX", "StateY", "CountryZ");
    }

    @Test
    void testGetAllAdress() {
        // Arrange: simula retorno do service
        when(adressService.getAllAdress()).thenReturn(List.of(adress));

        // Act
        ResponseEntity<List<Adress>> response = adressController.getAllAdress();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(adress, response.getBody().get(0));
        verify(adressService, times(1)).getAllAdress();
    }

    @Test
    void testCreateAdress() {
        // Arrange: simula retorno do service para criação de endereço
        when(adressService.saveAdress(adressDTO)).thenReturn(adress);

        // Act
        ResponseEntity<Adress> response = adressController.createAdress(adressDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adress, response.getBody());
        verify(adressService, times(1)).saveAdress(adressDTO);
    }

    @Test
    void testGetAdressByIdFound() {
        // Arrange: simula retorno de um Optional com o endereço
        when(adressService.findAdressById(1L)).thenReturn(Optional.of(adress));

        // Act
        ResponseEntity<Optional<Adress>> response = adressController.getAdressById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(adress, response.getBody().get());
        verify(adressService, times(1)).findAdressById(1L);
    }

    @Test
    void testGetAdressByIdNotFound() {
        // Arrange: simula Optional vazio para o id informado
        when(adressService.findAdressById(2L)).thenReturn(Optional.empty());

        // Act & Assert: deve lançar NoSuchElementException
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            adressController.getAdressById(2L);
        });
        assertEquals("Endereco não encontrado", exception.getMessage());
        verify(adressService, times(1)).findAdressById(2L);
    }

    @Test
    void testDeleteAdressByIdSuccess() {
        // Arrange: simula deleção sem exceção
        // (nenhum comportamento especial a ser mockado, já que nenhum retorno é esperado)
        doNothing().when(adressService).deleteAdress(1L);

        // Act
        ResponseEntity<String> response = adressController.deleteAdressById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Endereço removido com sucesso", response.getBody());
        verify(adressService, times(1)).deleteAdress(1L);
    }

    @Test
    void testDeleteAdressByIdNotFound() {
        // Arrange: simula exceção ao tentar deletar endereço inexistente
        String errorMessage = "Endereço não encontrado";
        doThrow(new IllegalArgumentException(errorMessage)).when(adressService).deleteAdress(2L);

        // Act
        ResponseEntity<String> response = adressController.deleteAdressById(2L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(adressService, times(1)).deleteAdress(2L);
    }
}