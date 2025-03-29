package com.qmasters.fila_flex.testSevice;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.AdressDTO;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.repository.AdressRepository;
import com.qmasters.fila_flex.service.AdressService;

class AdressServiceTest {

    private AdressService adressService;
    private AdressRepository adressRepositoryMock;

    @BeforeEach
    void setUp() {
        // Criação do mock do AdressRepository
        adressRepositoryMock = mock(AdressRepository.class);
        
        // Instanciação do AdressService com o mock do repositório
        adressService = new AdressService(adressRepositoryMock);
    }

    @Test
    void testSaveAdress() {
        // Dado que um AdressDTO é fornecido
        AdressDTO adressDTO = new AdressDTO("123", "Main Street", "City", "State", "Country");
        
        // Simulando a criação de um novo endereço
        Adress expectedAdress = new Adress("123", "Main Street", "City", "State", "Country");
        when(adressRepositoryMock.save(any(Adress.class))).thenReturn(expectedAdress);
        
        // Chamando o método de salvar
        Adress savedAdress = adressService.saveAdress(adressDTO);
        
        // Verificando se o endereço foi salvo corretamente
        assertNotNull(savedAdress);
        assertEquals("123", savedAdress.getNumber());
        assertEquals("Main Street", savedAdress.getStreet());
        assertEquals("City", savedAdress.getCity());
        assertEquals("State", savedAdress.getState());
        assertEquals("Country", savedAdress.getCountry());
        
        // Verificando se o método save foi chamado uma vez
        verify(adressRepositoryMock, times(1)).save(any(Adress.class));
    }

    @Test
    void testGetAllAdress() {
        // Dado que existem vários endereços no repositório
        Adress adress1 = new Adress("123", "Main Street", "City", "State", "Country");
        Adress adress2 = new Adress("456", "Second Street", "Another City", "State", "Country");
        when(adressRepositoryMock.findAll()).thenReturn(List.of(adress1, adress2));
        
        // Chamando o método para recuperar todos os endereços
        List<Adress> adressList = adressService.getAllAdress();
        
        // Verificando se os endereços foram retornados corretamente
        assertNotNull(adressList);
        assertEquals(2, adressList.size());
        assertEquals("123", adressList.get(0).getNumber());
        assertEquals("456", adressList.get(1).getNumber());
    }

    @Test
    void testFindAdressById() {
        // Dado que o endereço existe no repositório
        Long adressId = 1L;
        Adress adress = new Adress("123", "Main Street", "City", "State", "Country");
        when(adressRepositoryMock.findById(adressId)).thenReturn(Optional.of(adress));
        
        // Chamando o método para encontrar o endereço por ID
        Optional<Adress> foundAdress = adressService.findAdressById(adressId);
        
        // Verificando se o endereço foi encontrado corretamente
        assertTrue(foundAdress.isPresent());
        assertEquals("123", foundAdress.get().getNumber());
    }

    @Test
    void testFindAdressById_NotFound() {
        // Dado que o endereço não existe no repositório
        Long adressId = 1L;
        when(adressRepositoryMock.findById(adressId)).thenReturn(Optional.empty());
        
        // Chamando o método para encontrar o endereço por ID
        Optional<Adress> foundAdress = adressService.findAdressById(adressId);
        
        // Verificando se o endereço não foi encontrado
        assertFalse(foundAdress.isPresent());
    }

    @Test
    void testDeleteAdress() {
        // Dado que o endereço existe no repositório
        Long adressId = 1L;
        when(adressRepositoryMock.existsById(adressId)).thenReturn(true);
        
        // Chamando o método para deletar o endereço
        adressService.deleteAdress(adressId);
        
        // Verificando se o método delete foi chamado
        verify(adressRepositoryMock, times(1)).deleteById(adressId);
    }

    @Test
    void testDeleteAdress_NotFound() {
        // Dado que o endereço não existe no repositório
        Long adressId = 1L;
        when(adressRepositoryMock.existsById(adressId)).thenReturn(false);
        
        // Verificando se a exceção será lançada
        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            adressService.deleteAdress(adressId);
        });
        
        // Verificando a mensagem da exceção
        assertEquals("Endereço não encontrado, remoção não foi realizada", thrown.getMessage());
    }
}

