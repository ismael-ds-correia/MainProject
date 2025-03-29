package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.AppointmentTypeDetailsDTO;

class AppointmentTypeDetailsDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Dados de exemplo
        String name = "Consulta Psicológica";
        String description = "Atendimento para avaliação psicológica";
        List<String> category = List.of("Saúde Mental", "Terapia");
        double price = 150.0;
        LocalDate appointmentDate = LocalDate.of(2025, 5, 10);
        List<String> requiredDocumentation = List.of("RG", "Comprovante de Residência");

        // Criando a instância com o construtor parametrizado
        AppointmentTypeDetailsDTO dto = new AppointmentTypeDetailsDTO(name, description, category, price, appointmentDate, requiredDocumentation);

        // Verificações
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
        assertEquals(category, dto.getCategory());
        assertEquals(price, dto.getPrice());
        assertEquals(appointmentDate, dto.getAppointmentDate());
        assertEquals(requiredDocumentation, dto.getRequiredDocumentation());
    }

    @Test
    void testSetters() {
        AppointmentTypeDetailsDTO dto = new AppointmentTypeDetailsDTO();

        // Dados de teste
        dto.setName("Consulta Médica");
        dto.setDescription("Atendimento clínico geral");
        dto.setCategory(List.of("Saúde", "Clínico Geral"));
        dto.setPrice(200.0);
        dto.setAppointmentDate(LocalDate.of(2025, 6, 15));
        dto.setRequiredDocumentation(List.of("CPF", "Cartão do SUS"));

        // Verificações
        assertEquals("Consulta Médica", dto.getName());
        assertEquals("Atendimento clínico geral", dto.getDescription());
        assertEquals(List.of("Saúde", "Clínico Geral"), dto.getCategory());
        assertEquals(200.0, dto.getPrice());
        assertEquals(LocalDate.of(2025, 6, 15), dto.getAppointmentDate());
        assertEquals(List.of("CPF", "Cartão do SUS"), dto.getRequiredDocumentation());
    }
}
