package com.qmasters.fila_flex.testDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.util.PriorityCondition;

class AppointmentDTOTest {

    private AppointmentType appointmentType;
    private AppointmentTypeDetails appointmentTypeDetails;
    private User user;
    private LocalDateTime scheduledDateTime;
    private LocalDateTime createdDateTime;
    private Integer queueOrder;
    private PriorityCondition priorityCondition;

    @BeforeEach
    void setup() {
        // Criando um objeto AppointmentTypeDetails
        appointmentTypeDetails = new AppointmentTypeDetails(
            "Consulta Médica",
            "Consulta médica geral",
            List.of("Saúde", "Clínica"),
            100.0,
            LocalDate.now(),
            List.of("RG", "Comprovante de Residência")
        );

        // Criando um objeto AppointmentType e associando AppointmentTypeDetails
        appointmentType = new AppointmentType(appointmentTypeDetails, 30, new Adress("Rua ABC", "123", "Cidade", "Estado", "CEP"));

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        scheduledDateTime = LocalDateTime.now();
        createdDateTime = LocalDateTime.now().minusDays(1);
        queueOrder = 5;
        priorityCondition = PriorityCondition.NO_PRIORITY;
    }

    @Test
    void testConstructorWithQueueOrder() {
        AppointmentDTO dto = new AppointmentDTO(appointmentType, user, scheduledDateTime, createdDateTime);
        assertNotNull(dto);
        assertEquals("user@example.com", dto.getUserEmail());
        assertEquals("Consulta Médica", dto.getAppointmentTypeName());
        assertEquals("100.0", dto.getAppointmentTypePrice());
        assertEquals(30, Integer.parseInt(dto.getAppointmentTypeEstimatedTime()));
    }

    @Test
    void testConstructorWithPriorityCondition() {
        AppointmentDTO dto = new AppointmentDTO(appointmentType, user, scheduledDateTime, createdDateTime);
        dto.setQueueOrder(queueOrder);
        dto.setPriorityCondition(priorityCondition);
        assertNotNull(dto);
        assertEquals(priorityCondition, dto.getPriorityCondition());
        assertEquals(AppointmentStatus.MARKED, dto.getStatus());
    }

    @Test
    void testGettersAndSetters() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setUserId("2");
        dto.setUserEmail("newuser@example.com");
        dto.setAppointmentTypeName("Vacinação");
        dto.setAppointmentTypePrice("200.0");

        assertEquals("2", dto.getUserId());
        assertEquals("newuser@example.com", dto.getUserEmail());
        assertEquals("Vacinação", dto.getAppointmentTypeName());
        assertEquals("200.0", dto.getAppointmentTypePrice());
    }

    @Test
    void testGettersAndSettersForAppointmentTypeDetails() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentTypeCategory(new ArrayList<>(List.of("Saúde", "Odontologia")));
        dto.setAppointmentTypeRequiredDocumentation(new ArrayList<>(List.of("CPF", "Carteira de Saúde")));
        dto.setAppointmentTypeEstimatedTime("45 minutos");

        assertEquals(2, dto.getAppointmentTypeCategory().size());
        assertEquals("45 minutos", dto.getAppointmentTypeEstimatedTime());
        assertEquals("CPF", dto.getAppointmentTypeRequiredDocumentation().get(0));
    }

    @Test
    void testDefaultConstructor() {
        AppointmentDTO dto = new AppointmentDTO();
        assertNull(dto.getUser());  
        assertNull(dto.getAppointmentType());
        assertNull(dto.getQueueOrder());
        assertNull(dto.getPriorityCondition());
    }

    @Test
    void testSetStatus() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setStatus(AppointmentStatus.ATTENDING);
        assertEquals(AppointmentStatus.ATTENDING, dto.getStatus());
    }
}