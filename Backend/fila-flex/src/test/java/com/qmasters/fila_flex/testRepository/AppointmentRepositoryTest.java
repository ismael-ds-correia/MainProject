package com.qmasters.fila_flex.testRepository;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    public void setUp() {
        // Criar duas instâncias de agendamento com datas programadas diferentes
        appointment1 = new Appointment();
        appointment1.setScheduledDateTime(LocalDateTime.now().minusDays(1));
        appointment1.setStatus(AppointmentStatus.COMPLETED);

        appointment2 = new Appointment();
        appointment2.setScheduledDateTime(LocalDateTime.now().minusDays(2));
        appointment2.setStatus(AppointmentStatus.COMPLETED);

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);
    }

    @Test
    @Transactional
    public void testFindByScheduledDateTimeBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now();

        List<Appointment> appointments = appointmentRepository.findByScheduledDateTime(startDate, endDate);

        assertEquals(2, appointments.size(), "Deve retornar dois agendamentos.");
    }

    @Test
    @Transactional
    public void testFindByStatusInOrderByQueueOrder() {
        List<Appointment> appointments = appointmentRepository.findByStatusInOrderByQueueOrder(List.of(AppointmentStatus.COMPLETED));

        assertEquals(2, appointments.size(), "Deve retornar dois agendamentos com status 'COMPLETED'.");
    }
}
