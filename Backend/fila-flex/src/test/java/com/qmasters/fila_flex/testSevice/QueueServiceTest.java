package com.qmasters.fila_flex.testSevice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.qmasters.fila_flex.exception.CannotInsertOnQueueException;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.service.QueueService;
import com.qmasters.fila_flex.util.PriorityCondition;

@ExtendWith(MockitoExtension.class)
public class QueueServiceTest {

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private QueueService queueService;

    // ===================== Testes já existentes =====================

    @Test
    void testAssignQueuePosition() {
        Appointment appointment = new Appointment();
        AppointmentType appointmentType = new AppointmentType();
        appointment.setAppointmentType(appointmentType);
        appointmentType.setId(1L);

        when(appointmentTypeRepository.findNextQueueNumberForAppointmentType(1L)).thenReturn(5);
        
        Appointment result = queueService.assignQueuePosition(appointment);
        
        assertEquals(5, result.getQueueOrder());
    }

    @Test
    void testGetQueueByName() {
        List<Appointment> mockList = Arrays.asList(new Appointment(), new Appointment());
        when(appointmentTypeRepository.findByAppointmentTypeNameOrderByQueueOrder("Consulta"))
                .thenReturn(mockList);
        
        List<Appointment> result = queueService.getQueueByName("Consulta");
        assertEquals(2, result.size());
    }

    @Test
    void testReorganizeQueueAfterRemoval() {
        Appointment app1 = new Appointment();
        app1.setQueueOrder(3);
        Appointment app2 = new Appointment();
        app2.setQueueOrder(4);
        List<Appointment> subsequentApps = Arrays.asList(app1, app2);

        when(appointmentTypeRepository.findAllWithQueueOrderGreaterThan(1L, 2)).thenReturn(subsequentApps);

        queueService.reorganizeQueueAfterRemoval(1L, 2);

        assertEquals(2, app1.getQueueOrder());
        assertEquals(3, app2.getQueueOrder());
        verify(appointmentRepository, times(2)).save(any(Appointment.class));
    }

    @Test
    void testReorderQueueSamePosition() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(3);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        appointment.setAppointmentType(appointmentType);
        
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1L)).thenReturn(5);
        
        queueService.reorderQueue(10L, 3);
        
        assertEquals(3, appointment.getQueueOrder());
        verify(appointmentRepository, never()).save(argThat(app -> !app.getQueueOrder().equals(3)));
    }

    @Test
    void testReorderQueueMoveUp() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(4);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        appointment.setAppointmentType(appointmentType);

        when(appointmentRepository.findById(20L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1L)).thenReturn(5);

        Appointment other1 = new Appointment();
        other1.setQueueOrder(2);
        Appointment other2 = new Appointment();
        other2.setQueueOrder(3);
        List<Appointment> appointmentsToMoveUp = Arrays.asList(other1, other2);
        when(appointmentRepository.findAllWithPositionBetween(1L, 2, 3)).thenReturn(appointmentsToMoveUp);

        queueService.reorderQueue(20L, 2);

        assertEquals(2, appointment.getQueueOrder());
        assertEquals(3, other1.getQueueOrder());
        assertEquals(4, other2.getQueueOrder());
        verify(appointmentRepository, atLeast(3)).save(any(Appointment.class));
    }

    @Test
    void testReorderQueueMoveDown() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(2);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);
        appointment.setAppointmentType(appointmentType);

        when(appointmentRepository.findById(30L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1L)).thenReturn(5);

        Appointment other1 = new Appointment();
        other1.setQueueOrder(3);
        Appointment other2 = new Appointment();
        other2.setQueueOrder(4);
        List<Appointment> appointmentsToMoveDown = Arrays.asList(other1, other2);
        when(appointmentRepository.findAllWithPositionBetween(1L, 3, 4)).thenReturn(appointmentsToMoveDown);

        queueService.reorderQueue(30L, 4);

        assertEquals(4, appointment.getQueueOrder());
        assertEquals(2, other1.getQueueOrder());
        assertEquals(3, other2.getQueueOrder());
        verify(appointmentRepository, atLeast(3)).save(any(Appointment.class));
    }

    @Test
    void testInsertWithPriorityNoReposition() {
        // Cenário: appointment já está em posição privilegiada.
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(1);
        appointment.setPriorityCondition(PriorityCondition.ELDERLY);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(100L);
        appointment.setAppointmentType(appointmentType);

        // Simula que o primeiro appointment com NO_PRIORITY está na posição 3.
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(100L)).thenReturn(3);
        Appointment nonPriorityApp = new Appointment();
        nonPriorityApp.setQueueOrder(3);
        nonPriorityApp.setPriorityCondition(PriorityCondition.NO_PRIORITY);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(100L))
                .thenReturn(Arrays.asList(nonPriorityApp));
        
        when(appointmentRepository.findById(40L)).thenReturn(Optional.of(appointment));

        queueService.insertWithPriority(40L);

        // Espera que findById seja chamado apenas 1 vez e nenhum save de reposicionamento seja feito.
        verify(appointmentRepository, times(1)).findById(40L);
        verify(appointmentRepository, never()).save(argThat(app -> app.getQueueOrder() != 1));
    }

    @Test
    void testCallNextInQueueAppointmentNotFound() {
        when(appointmentRepository.findByAppointmentTypeIdAndQueueOrder(300L, 1))
                .thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            queueService.callNextInQueue(300L);
        });
        assertEquals("Não há agendamento na primeira posição da fila para esse tipo de serviço.", exception.getMessage());
    }

    @Test
    void testCallNextInQueueWrongStatus() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(1);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findByAppointmentTypeIdAndQueueOrder(400L, 1))
                .thenReturn(appointment);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            queueService.callNextInQueue(400L);
        });
        assertEquals("O agendamento na primeira posição não está disponível para atendimento.", exception.getMessage());
    }

    @Test
    void testCallNextInQueueSuccess() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(1);
        appointment.setStatus(AppointmentStatus.WAITING);
        when(appointmentRepository.findByAppointmentTypeIdAndQueueOrder(500L, 1))
                .thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArguments()[0]);

        Appointment next = queueService.callNextInQueue(500L);
        assertEquals(AppointmentStatus.ATTENDING, next.getStatus());
        assertEquals(0, next.getQueueOrder());
        assertNotNull(next.getStartTime());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void testCompleteAppointmentWrongStatus() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(1);
        appointment.setStatus(AppointmentStatus.WAITING);
        when(appointmentRepository.findById(600L)).thenReturn(Optional.of(appointment));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            queueService.completeAppointment(600L);
        });
        assertEquals("Não é possível finalizar um agendamento que não está em atendimento.", exception.getMessage());
    }

    @Test
    void testCompleteAppointmentSuccess() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(2);
        appointment.setStatus(AppointmentStatus.ATTENDING);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(700L);
        appointment.setAppointmentType(appointmentType);

        when(appointmentRepository.findById(7000L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArguments()[0]);
        when(appointmentTypeRepository.findAllWithQueueOrderGreaterThan(700L, 2))
                .thenReturn(Collections.emptyList());

        Appointment completed = queueService.completeAppointment(7000L);
        assertEquals(AppointmentStatus.COMPLETED, completed.getStatus());
        assertEquals(-1, completed.getQueueOrder());
        assertNotNull(completed.getEndTime());
        verify(appointmentRepository, atLeast(1)).save(appointment);
    }

    @Test
    void testRegisterCheckInWrongStatus() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.ATTENDING);
        when(appointmentRepository.findById(800L)).thenReturn(Optional.of(appointment));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            queueService.registerCheckIn(800L);
        });
        assertEquals("Não é possível registrar check-in para um agendamento que não está marcado.", exception.getMessage());
    }

    @Test
    void testRegisterCheckInSuccess() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.MARKED);
        when(appointmentRepository.findById(900L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArguments()[0]);

        Appointment checkedIn = queueService.registerCheckIn(900L);
        assertEquals(AppointmentStatus.WAITING, checkedIn.getStatus());
        assertNotNull(checkedIn.getCheckInTime());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    // ===================== Testes Adicionais para Cobertura Completa =====================

    // --- 1. Cobertura para findFirstNonPriorityPosition(AppointmentType) ---
    // Método privado; utilizamos reflection para invocá-lo.

    @Test
    void testFindFirstNonPriorityPosition_found() throws Exception {
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1000L);

        // Simula uma lista onde existe um appointment sem prioridade.
        Appointment app1 = new Appointment();
        app1.setQueueOrder(2);
        app1.setPriorityCondition(PriorityCondition.ELDERLY);
        Appointment app2 = new Appointment();
        app2.setQueueOrder(4);
        app2.setPriorityCondition(PriorityCondition.NO_PRIORITY);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(1000L))
                .thenReturn(Arrays.asList(app1, app2));

        Method method = QueueService.class.getDeclaredMethod("findFirstNonPriorityPosition", AppointmentType.class);
        method.setAccessible(true);
        int result = (Integer) method.invoke(queueService, appointmentType);
        // Deve retornar a queueOrder do app2, que é 4.
        assertEquals(4, result);
    }

    @Test
    void testFindFirstNonPriorityPosition_allHavePriority() throws Exception {
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1001L);

        // Simula lista onde todos têm prioridade.
        Appointment app1 = new Appointment();
        app1.setQueueOrder(2);
        app1.setPriorityCondition(PriorityCondition.ELDERLY);
        Appointment app2 = new Appointment();
        app2.setQueueOrder(3);
        app2.setPriorityCondition(PriorityCondition.ELDERLY);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(1001L))
                .thenReturn(Arrays.asList(app1, app2));
        // Stub para o máximo na fila.
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(1001L)).thenReturn(3);

        Method method = QueueService.class.getDeclaredMethod("findFirstNonPriorityPosition", AppointmentType.class);
        method.setAccessible(true);
        int result = (Integer) method.invoke(queueService, appointmentType);
        // Como nenhum tem NO_PRIORITY, espera-se o retorno de maxPosition + 1 = 4.
        assertEquals(4, result);
    }

    // --- 2. Cobertura para insertWithPriority(Long) em cenário de exceção ---
    @Test
    void testInsertWithPriorityAppointmentNotFound() {
        when(appointmentRepository.findById(9999L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> queueService.insertWithPriority(9999L));
    }

    // --- 3. Cobertura para validateNewPosition(Long, Integer) ---
    @Test
    void testReorderQueue_invalidNewPositionLow() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(3);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(10L);
        appointment.setAppointmentType(appointmentType);
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(10L)).thenReturn(5);
        // newPosition menor que 1
        assertThrows(IllegalArgumentException.class, () -> queueService.reorderQueue(100L, 0));
    }

    @Test
    void testReorderQueue_invalidNewPositionHigh() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(3);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(10L);
        appointment.setAppointmentType(appointmentType);
        when(appointmentRepository.findById(101L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(10L)).thenReturn(5);
        // newPosition maior que maxPosition
        assertThrows(IllegalArgumentException.class, () -> queueService.reorderQueue(101L, 6));
    }

    // --- 4. Cobertura para applyNewPosition(Long, int, AppointmentType) ---
    // Usaremos insertWithPriority para invocar applyNewPosition via calculatePriorityPosition.

    // Cenário: newPosition dentro do limite (newPosition <= maxPosition).
    @Test
    void testInsertWithPriority_applyNewPositionWithinBound() {
        // Cenário: O appointment necessita reposicionamento para uma posição dentro do limite.
        // Para que o método de reposicionamento seja acionado, o appointment não deve estar em posição privilegiada.
        // Ajustamos o queueOrder para 9, de forma que o primeiro appointment sem prioridade (queueOrder = 8) indique
        // que o appointment não está privilegiado.
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(9); // Alterado de 5 para 9
        appointment.setPriorityCondition(PriorityCondition.ELDERLY);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(200L);
        appointment.setAppointmentType(appointmentType);
        
        when(appointmentRepository.findById(50L)).thenReturn(Optional.of(appointment));
        // maxPosition é 10 e há um appointment sem prioridade na posição 8.
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(200L)).thenReturn(10);
        Appointment nonPriorityApp = new Appointment();
        nonPriorityApp.setQueueOrder(8);
        nonPriorityApp.setPriorityCondition(PriorityCondition.NO_PRIORITY);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(200L))
             .thenReturn(Arrays.asList(nonPriorityApp));
        
        QueueService spyQueueService = spy(queueService);
        doNothing().when(spyQueueService).reorderQueue(anyLong(), anyInt());
        
        spyQueueService.insertWithPriority(50L);
        // Espera-se que seja chamada com a posição 8, pois calculatePriorityPosition (findFirstNonPriorityPosition) retornará 8.
        verify(spyQueueService, times(1)).reorderQueue(50L, 8);
    }
    
    @Test
    void testInsertWithPriority_applyNewPositionExceedBound() {
        // Cenário: O appointment necessita reposicionamento, mas a nova posição calculada excede o limite da fila.
        // Para garantir o reposicionamento, o appointment deve estar em uma posição não privilegiada.
        // Ajustamos o queueOrder para 12 para que a condição de reposicionamento seja verdadeira.
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(12); // Alterado de 5 para 12
        appointment.setPriorityCondition(PriorityCondition.ELDERLY);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(300L);
        appointment.setAppointmentType(appointmentType);
        
        when(appointmentRepository.findById(60L)).thenReturn(Optional.of(appointment));
        // maxPosition é 10; como nenhum appointment com NO_PRIORITY é retornado, 
        // findFirstNonPriorityPosition retornará maxPosition + 1 (11), logo newPosition (11) excede o limite.
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(300L)).thenReturn(10);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(300L))
             .thenReturn(Arrays.asList(
                new Appointment() {{ setQueueOrder(1); setPriorityCondition(PriorityCondition.ELDERLY); }},
                new Appointment() {{ setQueueOrder(2); setPriorityCondition(PriorityCondition.ELDERLY); }}
             ));
        
        QueueService spyQueueService = spy(queueService);
        doNothing().when(spyQueueService).reorderQueue(anyLong(), anyInt());
        
        spyQueueService.insertWithPriority(60L);
        // Espera-se que seja chamada com a posição maxPosition (10), pois newPosition (11) excede o limite.
        verify(spyQueueService, times(1)).reorderQueue(60L, 10);
    }
    // --- 5. Cobertura para shouldRepositionAppointment(Appointment, AppointmentType) ---
    // Este método já é indiretamente testado, mas pode-se invocar via reflection se desejado.
    // Exemplo: quando a fila está vazia (maxPosition == null ou 0), deve retornar false.
    @Test
    void testShouldRepositionAppointment_emptyQueue() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(5);
        appointment.setPriorityCondition(PriorityCondition.ELDERLY);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(400L);
        appointment.setAppointmentType(appointmentType);

        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(400L)).thenReturn(0);
        
        Method method = QueueService.class.getDeclaredMethod("shouldRepositionAppointment", Appointment.class, AppointmentType.class);
        method.setAccessible(true);
        boolean result = (Boolean) method.invoke(queueService, appointment, appointmentType);
        // Se a fila estiver vazia, não reposiciona.
        assertFalse(result);
    }

    @Test
    void testInsertWithPriority_ExceptionWrapping() {
        Appointment appointment = new Appointment();
        // Ajuste: definir queueOrder maior que o primeiro appointment sem prioridade
        appointment.setQueueOrder(10);
        appointment.setPriorityCondition(PriorityCondition.ELDERLY);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(400L);
        appointment.setAppointmentType(appointmentType);
        
        // Stub para retornar o appointment e configurar a fila para reposicionamento.
        when(appointmentRepository.findById(1000L)).thenReturn(Optional.of(appointment));
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(400L)).thenReturn(10);
        Appointment nonPriorityApp = new Appointment();
        nonPriorityApp.setQueueOrder(8);
        nonPriorityApp.setPriorityCondition(PriorityCondition.NO_PRIORITY);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(400L))
                .thenReturn(Arrays.asList(nonPriorityApp));
        
        // Use um spy para forçar uma exceção na chamada de reorderQueue.
        QueueService spyQueue = spy(queueService);
        doThrow(new RuntimeException("Test exception")).when(spyQueue).reorderQueue(anyLong(), anyInt());
        
        CannotInsertOnQueueException ex = assertThrows(CannotInsertOnQueueException.class, () -> {
            spyQueue.insertWithPriority(1000L);
        });
        assertTrue(ex.getMessage().contains("Erro ao processar prioridade: Test exception"));
    }

// Testes para shouldRepositionAppointment usando reflection.

@Test
void testShouldRepositionAppointment_alreadyPrivileged() throws Exception {
    // Cenário: appointment com posição abaixo do primeiro não prioritário,
    // portanto, já está em posição privilegiada e NÃO deve ser reposicionado.
    Appointment appointment = new Appointment();
    appointment.setQueueOrder(2);
    appointment.setPriorityCondition(PriorityCondition.ELDERLY);
    AppointmentType appointmentType = new AppointmentType();
    appointmentType.setId(500L);
    appointment.setAppointmentType(appointmentType);
    
    when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(500L)).thenReturn(10);
    // Configura para que o primeiro appointment sem prioridade seja de queueOrder 3.
    Appointment nonPriority = new Appointment();
    nonPriority.setQueueOrder(3);
    nonPriority.setPriorityCondition(PriorityCondition.NO_PRIORITY);
    when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(500L))
            .thenReturn(Arrays.asList(nonPriority));
    
    Method method = QueueService.class.getDeclaredMethod("shouldRepositionAppointment", Appointment.class, AppointmentType.class);
    method.setAccessible(true);
    // Aqui, currentPosition (2) < firstNonPriorityPosition (3) e o appointment NÃO é NO_PRIORITY,
    // logo, a condição interna é verdadeira e, ao negar, espera-se false.
    boolean result = (Boolean) method.invoke(queueService, appointment, appointmentType);
    assertFalse(result);
}

    @Test
    void testShouldRepositionAppointment_needsReposition() throws Exception {
        // Cenário: appointment com posição igual ou acima do primeiro não prioritário,
        // portanto, necessita de reposicionamento.
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(4);
        appointment.setPriorityCondition(PriorityCondition.ELDERLY);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(600L);
        appointment.setAppointmentType(appointmentType);
        
        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(600L)).thenReturn(10);
        // Configura para que o primeiro appointment sem prioridade seja de queueOrder 3.
        Appointment nonPriority = new Appointment();
        nonPriority.setQueueOrder(3);
        nonPriority.setPriorityCondition(PriorityCondition.NO_PRIORITY);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(600L))
                .thenReturn(Arrays.asList(nonPriority));
        
        Method method = QueueService.class.getDeclaredMethod("shouldRepositionAppointment", Appointment.class, AppointmentType.class);
        method.setAccessible(true);
        // Aqui, currentPosition (4) não é menor que firstNonPriorityPosition (3), logo a condição interna é false,
        // e o método retorna !false = true.
        boolean result = (Boolean) method.invoke(queueService, appointment, appointmentType);
        assertTrue(result);
    }


    // Testes para moveAppointmentsDown e moveAppointmentsUp usando reflection.

    // Teste para moveAppointmentsDown
    @Test
    void testMoveAppointmentsDown() throws Exception {
        // Configura uma lista com appointments a serem reposicionados.
        Appointment app1 = new Appointment();
        app1.setQueueOrder(4);
        Appointment app2 = new Appointment();
        app2.setQueueOrder(5);
        List<Appointment> list = Arrays.asList(app1, app2);
        
        // Suponha que o método seja chamado com fromPosition = 2 e toPosition = 4,
        // o método deverá buscar appointments com posição entre 3 e 4 (fromPosition+1 até toPosition).
        when(appointmentRepository.findAllWithPositionBetween(700L, 3, 4)).thenReturn(list);
        
        Method method = QueueService.class.getDeclaredMethod("moveAppointmentsDown", Long.class, Integer.class, Integer.class);
        method.setAccessible(true);
        method.invoke(queueService, 700L, 2, 4);
        
        // Cada appointment tem sua queueOrder decrementada em 1.
        assertEquals(3, app1.getQueueOrder());
        assertEquals(4, app2.getQueueOrder());
        verify(appointmentRepository, times(2)).save(any(Appointment.class));
    }

    // Teste para moveAppointmentsUp
    @Test
    void testMoveAppointmentsUp() throws Exception {
        // Configura uma lista com appointments a serem reposicionados.
        Appointment app1 = new Appointment();
        app1.setQueueOrder(2);
        Appointment app2 = new Appointment();
        app2.setQueueOrder(3);
        List<Appointment> list = Arrays.asList(app1, app2);
        
        // Suponha que o método seja chamado com fromPosition = 1 e toPosition = 3,
        // o método deverá buscar appointments com posição entre 1 e 2.
        when(appointmentRepository.findAllWithPositionBetween(800L, 1, 2)).thenReturn(list);
        
        Method method = QueueService.class.getDeclaredMethod("moveAppointmentsUp", Long.class, Integer.class, Integer.class);
        method.setAccessible(true);
        method.invoke(queueService, 800L, 1, 3);
        
        // Cada appointment tem sua queueOrder incrementada em 1.
        assertEquals(3, app1.getQueueOrder());
        assertEquals(4, app2.getQueueOrder());
        verify(appointmentRepository, times(2)).save(any(Appointment.class));
    }

    @Test
    void testCallNextInQueueStatusMarked() {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(1);
        appointment.setStatus(AppointmentStatus.MARKED); // Status MARKED
        when(appointmentRepository.findByAppointmentTypeIdAndQueueOrder(700L, 1)).thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArguments()[0]);

        Appointment result = queueService.callNextInQueue(700L);
        assertEquals(AppointmentStatus.ATTENDING, result.getStatus());
        assertEquals(0, result.getQueueOrder());
        assertNotNull(result.getStartTime());
        verify(appointmentRepository, times(1)).save(appointment);
    }


    // --- Testes adicionais para shouldRepositionAppointment ---
    // Este método é privado. Usamos reflection para invocá-lo.

    // Cenário: a fila está vazia pois o maxPosition é null – o método deve retornar false.
    @Test
    void testShouldRepositionAppointment_emptyQueueMaxPositionNull() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(5);
        appointment.setPriorityCondition(PriorityCondition.ELDERLY);
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(7000L);
        appointment.setAppointmentType(appointmentType);

        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(7000L)).thenReturn(null);

        Method method = QueueService.class.getDeclaredMethod("shouldRepositionAppointment", Appointment.class, AppointmentType.class);
        method.setAccessible(true);
        boolean result = (Boolean) method.invoke(queueService, appointment, appointmentType);
        assertFalse(result);
    }

    // Cenário: O appointment NÃO tem NO_PRIORITY e está em posição privilegiada 
    // (currentPosition < firstNonPriorityPosition) – o método deve retornar false.
    

    // Cenário: O appointment tem NO_PRIORITY mesmo estando em posição privilegiada – deve retornar true.
    @Test
    void testShouldRepositionAppointment_noPriorityAndPrivileged() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setQueueOrder(2); // Em posição privilegiada
        appointment.setPriorityCondition(PriorityCondition.NO_PRIORITY); // Sem prioridade
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(9000L);
        appointment.setAppointmentType(appointmentType);

        when(appointmentTypeRepository.findMaxQueueOrderForAppointmentType(9000L)).thenReturn(10);
        // Simula que o primeiro appointment sem prioridade está na posição 3.
        Appointment nonPriority = new Appointment();
        nonPriority.setQueueOrder(3);
        nonPriority.setPriorityCondition(PriorityCondition.NO_PRIORITY);
        when(appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(9000L))
                .thenReturn(Arrays.asList(nonPriority));

        Method method = QueueService.class.getDeclaredMethod("shouldRepositionAppointment", Appointment.class, AppointmentType.class);
        method.setAccessible(true);
        // Aqui, mesmo que currentPosition (2) < 3, como o appointment é NO_PRIORITY, a condição interna 
        // é falsa e sua negação retorna true (deve reposicionar).
        boolean result = (Boolean) method.invoke(queueService, appointment, appointmentType);
        assertTrue(result);
    }
}