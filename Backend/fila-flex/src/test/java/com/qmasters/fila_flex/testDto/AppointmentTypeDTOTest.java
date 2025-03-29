package com.qmasters.fila_flex.testDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;

class AppointmentTypeDTOTest {

    private AppointmentTypeDTO appointmentTypeDTO;
    private Adress adress;

    @BeforeEach
    void setUp() {
        // Inicialização dos objetos antes de cada teste
        adress = new Adress();
        adress.setStreet("Rua ABC");
        adress.setNumber("123");
        adress.setCity("Cidade");
        adress.setState("Estado");
        adress.setCountry("Brasil");

        AppointmentTypeDetails details = new AppointmentTypeDetails();
        details.setName("Consulta Médica");
        details.setDescription("Consulta geral");
        details.setCategory(Arrays.asList("Saúde", "Clínica"));
        details.setPrice(100.0);
        details.setAppointmentDate(LocalDate.now());
        details.setRequiredDocumentation(Arrays.asList("RG", "Comprovante de Residência"));

        // Usar o construtor atual do DTO
        appointmentTypeDTO = new AppointmentTypeDTO(
            details, 
            30, 
            adress
        );
    }

    @Test
    void testConstructor() {
        // Verifica se o construtor está funcionando corretamente
        assertNotNull(appointmentTypeDTO);
        assertEquals("Consulta Médica", appointmentTypeDTO.getName());
        assertEquals("Consulta geral", appointmentTypeDTO.getDescription());
        assertEquals(100.0, appointmentTypeDTO.getPrice());
        assertEquals(30, appointmentTypeDTO.getEstimatedTime());
        assertEquals(LocalDate.now(), appointmentTypeDTO.getAppointmentDate());
        assertEquals(2, appointmentTypeDTO.getRequiredDocumentation().size());
        assertEquals("Rua ABC", appointmentTypeDTO.getAdress().getStreet());
    }

    @Test
    void testSettersAndGetters() {
        // Teste dos setters e getters
        appointmentTypeDTO.setName("Vacinação");
        appointmentTypeDTO.setDescription("Vacinação contra gripe");
        appointmentTypeDTO.setCategory(Arrays.asList("Vacina", "Saúde"));
        appointmentTypeDTO.setPrice(150.0);
        appointmentTypeDTO.setEstimatedTime(20);
        appointmentTypeDTO.setAppointmentDate(LocalDate.now().plusDays(1));
        appointmentTypeDTO.setRequiredDocumentation(Arrays.asList("Cartão de Vacinas"));
        
        Adress newAdress = new Adress();
        newAdress.setStreet("Rua XYZ");
        newAdress.setNumber("456");
        newAdress.setCity("Nova Cidade");
        newAdress.setState("Novo Estado");
        newAdress.setCountry("Novo País");

        appointmentTypeDTO.setAdress(newAdress);

        // Verificar se os valores foram corretamente definidos
        assertEquals("Vacinação", appointmentTypeDTO.getName());
        assertEquals("Vacinação contra gripe", appointmentTypeDTO.getDescription());
        assertEquals(150.0, appointmentTypeDTO.getPrice());
        assertEquals(20, appointmentTypeDTO.getEstimatedTime());
        assertEquals(LocalDate.now().plusDays(1), appointmentTypeDTO.getAppointmentDate());
        assertEquals(1, appointmentTypeDTO.getRequiredDocumentation().size());
        assertEquals("Rua XYZ", appointmentTypeDTO.getAdress().getStreet());
    }

    @Test
    void testAdressGetterAndSetter() {
        // Teste para verificar se o getter e setter de "Adress" funcionam corretamente
        Adress testAdress = new Adress();
        testAdress.setStreet("Rua Teste");
        testAdress.setNumber("999");
        testAdress.setCity("Cidade Teste");
        testAdress.setState("Estado Teste");
        testAdress.setCountry("País Teste");

        appointmentTypeDTO.setAdress(testAdress);

        assertNotNull(appointmentTypeDTO.getAdress());
        assertEquals("Rua Teste", appointmentTypeDTO.getAdress().getStreet());
        assertEquals("Cidade Teste", appointmentTypeDTO.getAdress().getCity());
        assertEquals("Estado Teste", appointmentTypeDTO.getAdress().getState());
        assertEquals("País Teste", appointmentTypeDTO.getAdress().getCountry());
    }

    @Test
    void testGetCategory() {
        // Verificando o getter para category
        assertNotNull(appointmentTypeDTO.getCategory());
        assertEquals(2, appointmentTypeDTO.getCategory().size());
        assertEquals("Saúde", appointmentTypeDTO.getCategory().get(0));
    }

    @Test
    void testGetRequiredDocumentation() {
        // Verificando o getter para requiredDocumentation
        assertNotNull(appointmentTypeDTO.getRequiredDocumentation());
        assertEquals(2, appointmentTypeDTO.getRequiredDocumentation().size());
        assertEquals("RG", appointmentTypeDTO.getRequiredDocumentation().get(0));
    }


    @Test
    void testSetCategory() {
        AppointmentTypeDetails details = new AppointmentTypeDetails("Consulta", "Descrição", new ArrayList<>(), 100.0, null, new ArrayList<>());
        AppointmentTypeDTO dto = new AppointmentTypeDTO(details, 30, adress);

        dto.setCategory("Consulta Médica");

        assertNotNull(dto.getCategory());
        assertEquals(1, dto.getCategory().size());
        assertEquals("Consulta Médica", dto.getCategory().get(0));
    }

    @Test
    void testSetAppointmentTypeDetails() {
        AppointmentTypeDTO dto = new AppointmentTypeDTO();
        AppointmentTypeDetails details = new AppointmentTypeDetails("Consulta", "Descrição", new ArrayList<>(), 100.0, null, new ArrayList<>());

        dto.setAppointmentTypeDetails(details);
        assertEquals(details, dto.getAppointmentTypeDetails());
    }

    @Test
    void testGetAppointmentTypeDetails() {
        AppointmentTypeDetails details = new AppointmentTypeDetails("Consulta", "Descrição", new ArrayList<>(), 100.0, null, new ArrayList<>());
        AppointmentTypeDTO dto = new AppointmentTypeDTO();
        dto.setAppointmentTypeDetails(details);

        assertNotNull(dto.getAppointmentTypeDetails());
        assertEquals("Consulta", dto.getAppointmentTypeDetails().getName());
        assertEquals("Descrição", dto.getAppointmentTypeDetails().getDescription());
    }
}
