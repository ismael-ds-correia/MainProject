package com.qmasters.fila_flex.tertSevice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.qmasters.fila_flex.service.QueueService;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.util.PriorityCondition;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private QueueService queueService;

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
    void testAssignQueuePosition() {
        when(appointmentTypeRepository.findNextQueueNumberForAppointmentType(1L)).thenReturn(2);

        Appointment result = queueService.assignQueuePosition(appointment);

        assertEquals(2, result.getQueueOrder());
        verify(appointmentTypeRepository).findNextQueueNumberForAppointmentType(1L);
    }

    @Test
    void testGetQueueByName() {
        List<Appointment> mockList = Collections.singletonList(appointment);
        when(appointmentTypeRepository.findByAppointmentTypeNameOrderByQueueOrder("TestType")).thenReturn(mockList);

        List<Appointment> result = queueService.getQueueByName("TestType");

        assertEquals(mockList, result);
        verify(appointmentTypeRepository).findByAppointmentTypeNameOrderByQueueOrder("TestType");
    }

    @Test
    void testReorganizeQueueAfterRemoval() {
        Appointment subsequent = new Appointment();
        subsequent.setQueueOrder(2);
        List<Appointment> subsequentAppointments = Collections.singletonList(subsequent);
        when(appointmentTypeRepository.findAllWithQueueOrderGreaterThan(1L, 1)).thenReturn(subsequentAppointments);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(subsequent);

        queueService.reorganizeQueueAfterRemoval(1L, 1);

        assertEquals(1, subsequent.getQueueOrder());
        verify(appointmentRepository).save(subsequent);
    }

    @Test
    void testReorderQueue_ValidMove() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1L)).thenReturn(2);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        queueService.reorderQueue(1L, 2);

        verify(appointmentRepository, times(2)).save(appointment);
        assertEquals(2, appointment.getQueueOrder());
    }

    @Test
    void testReorderQueue_InvalidPosition() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1L)).thenReturn(2);

        assertThrows(IllegalArgumentException.class, () -> queueService.reorderQueue(1L, 3));
    }

    @Test
    void testCallNextInQueue_Success() {
        appointment.setStatus(AppointmentStatus.WAITING);
        when(appointmentRepository.findByAppointmentTypeIdAndQueueOrder(1L, 1)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment result = queueService.callNextInQueue(1L);

        assertEquals(AppointmentStatus.ATTENDING, result.getStatus());
        assertNotNull(result.getStartTime());
        assertEquals(-1, result.getQueueOrder());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void testCallNextInQueue_NoAppointment() {
        when(appointmentRepository.findByAppointmentTypeIdAndQueueOrder(1L, 1)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> queueService.callNextInQueue(1L));
    }

    @Test
    void testCompleteAppointment_Success() {
        appointment.setStatus(AppointmentStatus.ATTENDING);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment result = queueService.completeAppointment(1L);

        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
        assertEquals(-1, result.getQueueOrder());
        assertNotNull(result.getEndTime());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void testCompleteAppointment_NotAttending() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(IllegalStateException.class, () -> queueService.completeAppointment(1L));
    }

    @Test
    void testRegisterCheckIn_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment result = queueService.registerCheckIn(1L);

        assertEquals(AppointmentStatus.WAITING, result.getStatus());
        assertNotNull(result.getCheckInTime());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void testInsertWithPriority_Success() {
        appointment.setPriorityCondition(PriorityCondition.PWD);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1L)).thenReturn(2);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(1L))
                .thenReturn(Collections.singletonList(appointment));

        queueService.insertWithPriority(1L);

        verify(appointmentRepository, atLeastOnce()).findById(1L);
    }

    @Test
    void testInsertWithPriority_NoRepositionNeeded() {
        appointment.setPriorityCondition(PriorityCondition.PWD);
        appointment.setQueueOrder(1);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1L)).thenReturn(2);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(1L))
                .thenReturn(Collections.singletonList(appointment));

        queueService.insertWithPriority(1L);

        verifyNoMoreInteractions(appointmentRepository); // Não deve salvar nada
    }
}