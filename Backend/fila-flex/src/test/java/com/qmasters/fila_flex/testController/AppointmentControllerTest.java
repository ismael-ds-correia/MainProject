
package com.qmasters.fila_flex.testController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.qmasters.fila_flex.util.UserRole; // Importa o enum UserRole
import java.util.List; // Importa List de java.util
import java.util.Arrays; // Importa Arrays se necessário para instanciar List
import org.junit.jupiter.api.BeforeEach; // Importa o BeforeEach para configuração do método
import org.mockito.InjectMocks; // Importa o InjectMocks para injeção dos mocks
import org.mockito.Mock; // Importa o Mock para criar mocks
import org.mockito.MockitoAnnotations; // Importa para inicializar os mocks
import org.springframework.http.MediaType; // Importa MediaType para tipos de conteúdo
import org.springframework.test.web.servlet.MockMvc; // Importa MockMvc para realizar requisições
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders; // Importa os construtores de requisição
import org.springframework.test.web.servlet.setup.MockMvcBuilders; // Importa para configurar o MockMvc
import com.fasterxml.jackson.databind.ObjectMapper; // Importa ObjectMapper para manipulação de JSON

import com.qmasters.fila_flex.controller.AppointmentController;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AppointmentControllerTest {

    @InjectMocks
    private AppointmentController appointmentController;

    @Mock
    private AppointmentService appointmentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private AppointmentDTO appointmentDTO;
    private Appointment appointment;
    private User user;
    private AppointmentType appointmentType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
        objectMapper = new ObjectMapper();

        // Criação de objetos para simulação
        user = new User("email@example.com", "12345678", UserRole.USER, "Test User");
        user.setId(1L);

        appointmentType = new AppointmentType();
        appointmentType.setName("Consulta");
        appointmentType.setDescription("Consulta médica");

        appointmentDTO = new AppointmentDTO(appointmentType, user, LocalDateTime.now().plusDays(1), LocalDateTime.now());
        appointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
        appointment.setId(1L);
    }

    @Test
    void getAllAppointment() throws Exception {
        // Mock para retornar uma lista de agendamentos
        when(appointmentService.getAllAppointment()).thenReturn(List.of(appointment));

        mockMvc.perform(get("/appointment/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].appointmentTypeName").value("Consulta"));
    }

    @Test
    void getAppointmentById() throws Exception {
        // Mock para encontrar um agendamento pelo ID
        when(appointmentService.findAppointmentById(eq(1L))).thenReturn(Optional.of(appointment));

        mockMvc.perform(get("/appointment/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.appointmentTypeName").value("Consulta"));
    }

    @Test
    void getAppointmentsByUserId() throws Exception {
        // Mock para retornar agendamentos por userId
        when(appointmentService.findFullAppointmentsByUserId(eq(1L))).thenReturn(List.of(appointment));

        mockMvc.perform(get("/appointment/user")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAppointmentsByUserIdBadRequest() throws Exception {
        // Mock para lançar exceção
        when(appointmentService.findFullAppointmentsByUserId(eq(1L)))
                .thenThrow(new IllegalArgumentException("Erro ao buscar agendamentos"));

        mockMvc.perform(get("/appointment/user")
                .param("userId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao buscar agendamentos"));
    }

    @Test
    void getAppointmentBetweenDates() throws Exception {
        // Mock para buscar agendamentos entre datas
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(appointmentService.findByScheduledDateTime(eq(startDate), eq(endDate))).thenReturn(List.of(appointment));

        mockMvc.perform(get("/appointment/between")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}