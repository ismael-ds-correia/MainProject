package com.qmasters.fila_flex.testDto;
//vai precisar refazer com os padroes de codigo correto 
/*package com.qmasters.fila_flex;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.model.Adress;

class AppointmentTypeDTOTest {

    @Test
    void testConstrutorComParametros() {
        LocalDate data = LocalDate.of(2025, 3, 19);
        List<String> categorias = Arrays.asList("Saúde", "Beleza");
        List<String> documentos = Arrays.asList("RG", "Comprovante de Endereço");
        Adress endereco = new Adress("Rua Exemplo", "123", "Bairro Exemplo", "Cidade Exemplo", "Estado Exemplo");

        AppointmentTypeDTO appointment = new AppointmentTypeDTO(
            "Consulta",
            "Consulta médica geral",
            categorias,
            150.0,
            30,
            data,
            documentos,
            endereco
        );

        assertEquals("Consulta", appointment.getName());
        assertEquals("Consulta médica geral", appointment.getDescription());
        assertEquals(categorias, appointment.getCategory());
        assertEquals(150.0, appointment.getPrice());
        assertEquals(30, appointment.getEstimatedTime());
        assertEquals(data, appointment.getAppointmentDate());
        assertEquals(documentos, appointment.getRequiredDocumentation());
        assertEquals(endereco, appointment.getAdress());
    }

    @Test
    void testSettersEGetters() {
        AppointmentTypeDTO appointment = new AppointmentTypeDTO();
        
        LocalDate novaData = LocalDate.of(2025, 4, 10);
        List<String> novasCategorias = Arrays.asList("Educação", "Tecnologia");
        List<String> novosDocumentos = Arrays.asList("CPF", "Certificado");
        Adress novoEndereco = new Adress("Avenida Principal", "456", "Bairro Principal", "Cidade Principal", "Estado Principal");

        appointment.setName("Aula Particular");
        appointment.setDescription("Aula de programação");
        appointment.setCategory(novasCategorias);
        appointment.setPrice(200.0);
        appointment.setEstimatedTime(60);
        appointment.setAppointmentDate(novaData);
        appointment.setRequiredDocumentation(novosDocumentos);
        appointment.setAdress(novoEndereco);

        assertEquals("Aula Particular", appointment.getName());
        assertEquals("Aula de programação", appointment.getDescription());
        assertEquals(novasCategorias, appointment.getCategory());
        assertEquals(200.0, appointment.getPrice());
        assertEquals(60, appointment.getEstimatedTime());
        assertEquals(novaData, appointment.getAppointmentDate());
        assertEquals(novosDocumentos, appointment.getRequiredDocumentation());
        assertEquals(novoEndereco, appointment.getAdress());
    }
}*/