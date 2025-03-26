package com.qmasters.fila_flex.testController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.qmasters.fila_flex.controller.ReportController;
import com.qmasters.fila_flex.service.ReportService;
import com.qmasters.fila_flex.model.Appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

public class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportController = new ReportController(reportService);
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
    }

    @Test
    void testGetAppointmentsByPeriod() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        Appointment appointment = new Appointment();
        when(reportService.getAppointmentsByPeriod(startDate, endDate)).thenReturn(Arrays.asList(appointment));

        mockMvc.perform(get("/reports/appointments")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }
}
