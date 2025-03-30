package com.qmasters.fila_flex.testDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.model.AppointmentType;

class EvaluationDTOTest {

    @Test
    void testConstructorWithParameters() {
        // Criar um AppointmentType
        AppointmentType appointmentType = new AppointmentType();

        // Criar o DTO usando o novo construtor
        EvaluationDTO evaluationDTO = new EvaluationDTO(5, "Excellent service", appointmentType);

        // Verificar se os valores foram corretamente atribuídos
        assertEquals(5, evaluationDTO.getRating());
        assertEquals("Excellent service", evaluationDTO.getComment());
        assertEquals(appointmentType, evaluationDTO.getAppointmentType());
    }

    @Test
    void testSettersAndGetters() {
        // Criar o DTO
        EvaluationDTO evaluationDTO = new EvaluationDTO(0, null, null);

        // Criar um AppointmentType
        AppointmentType appointmentType = new AppointmentType();

        // Definir valores usando setters (se existirem)
        evaluationDTO.setRating(4);
        evaluationDTO.setComment("Good service");
        evaluationDTO.setAppointmentType(appointmentType);

        // Verificar se os valores foram corretamente atribuídos
        assertEquals(4, evaluationDTO.getRating());
        assertEquals("Good service", evaluationDTO.getComment());
        assertEquals(appointmentType, evaluationDTO.getAppointmentType());
    }
}
