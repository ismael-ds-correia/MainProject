/*package com.qmasters.fila_flex.testController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.exception.CannotFindQueueException;
import com.qmasters.fila_flex.controller.QueueController;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.service.QueueService;
import com.qmasters.fila_flex.util.PriorityCondition;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class QueueControllerTest {

    @Mock
    private QueueService queueService;

    @InjectMocks
    private QueueController queueController;

    private Appointment appointment;
    private AppointmentType appointmentType;

    @BeforeEach
    void setUp() {
        appointmentType = new AppointmentType();
        appointmentType.setId(1L);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentType(appointmentType);
        appointment.setQueueOrder(1);
        appointment.setStatus(AppointmentStatus.MARKED);
        appointment.setPriorityCondition(PriorityCondition.NO_PRIORITY);
    }

    @Test
    void testGetQueueByAppointmentType_Success() {
        List<Appointment> queue = Collections.singletonList(appointment);
        when(queueService.getQueueByName("TestType")).thenReturn(queue);

        ResponseEntity<List<Appointment>> response = queueController.getQueueByAppointmentType("TestType");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(queue, response.getBody());
        verify(queueService).getQueueByName("TestType");
    }

    @Test
    void testGetQueueByAppointmentType_EmptyQueue() {
        when(queueService.getQueueByName("TestType")).thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class, () -> queueController.getQueueByAppointmentType("TestType"));

        verify(queueService).getQueueByName("TestType");
    }

    @Test
    void testGetQueueByAppointmentType_GenericException() {
        when(queueService.getQueueByName("TestType")).thenThrow(new RuntimeException("Erro inesperado"));

        assertThrows(CannotFindQueueException.class, () -> queueController.getQueueByAppointmentType("TestType"));

        verify(queueService).getQueueByName("TestType");
    }

    @Test
    void testReorderQueue_Success() {
        doNothing().when(queueService).reorderQueue(1L, 2);

        ResponseEntity<String> response = queueController.reorderQueue(1L, 2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Agendamento reordenado com sucesso", response.getBody());
        verify(queueService).reorderQueue(1L, 2);
    }

    @Test
    void testReorderQueue_InvalidPosition() {
        doThrow(new IllegalArgumentException("Posição inválida")).when(queueService).reorderQueue(1L, 3);

        ResponseEntity<String> response = queueController.reorderQueue(1L, 3);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Posição inválida", response.getBody());
        verify(queueService).reorderQueue(1L, 3);
    }

    @Test
    void testCallNextAppointment_Success() {
        appointment.setStatus(AppointmentStatus.ATTENDING);
        appointment.setStartTime(LocalDateTime.now());
        when(queueService.callNextInQueue(1L)).thenReturn(appointment);

        ResponseEntity<Appointment> response = queueController.callNextAppointment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointment, response.getBody());
        verify(queueService).callNextInQueue(1L);
    }

    @Test
    void testCallNextAppointment_NotFound() {
        when(queueService.callNextInQueue(1L)).thenThrow(new NoSuchElementException("Não há agendamento"));

        ResponseEntity<Appointment> response = queueController.callNextAppointment(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(queueService).callNextInQueue(1L);
    }

    @Test
    void testCompleteAppointment_Success() {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setEndTime(LocalDateTime.now());
        when(queueService.completeAppointment(1L)).thenReturn(appointment);

        ResponseEntity<Appointment> response = queueController.completeAppointment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointment, response.getBody());
        verify(queueService).completeAppointment(1L);
    }

    @Test
    void testCompleteAppointment_BadRequest() {
        when(queueService.completeAppointment(1L))
                .thenThrow(new IllegalStateException("Não está em atendimento"));

        ResponseEntity<Appointment> response = queueController.completeAppointment(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(queueService).completeAppointment(1L);
    }

    @Test
    void testRegisterCheckIn_Success() {
        appointment.setStatus(AppointmentStatus.WAITING);
        appointment.setCheckInTime(LocalDateTime.now());
        when(queueService.registerCheckIn(1L)).thenReturn(appointment);

        ResponseEntity<Appointment> response = queueController.registerCheckIn(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointment, response.getBody());
        verify(queueService).registerCheckIn(1L);
    }

    @Test
    void testRegisterCheckIn_NotFound() {
        when(queueService.registerCheckIn(1L))
                .thenThrow(new NoSuchElementException("Agendamento não encontrado"));

        ResponseEntity<Appointment> response = queueController.registerCheckIn(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(queueService).registerCheckIn(1L);
    }
}*/