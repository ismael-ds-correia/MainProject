package com.qmasters.fila_flex.testModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.model.Evaluation;

class AppointmentTypeTest {
    private AppointmentType appointmentType;
    private AppointmentTypeDetails mockDetails;

    @BeforeEach
    void setup() {
        mockDetails = mock(AppointmentTypeDetails.class);
        appointmentType = new AppointmentType(mockDetails, 30, new Adress());
    }

    @Test
    void testSetName() {
        appointmentType.setName("New Name");
        verify(mockDetails).setName("New Name");
    }

    @Test
    void testGetName() {
        when(mockDetails.getName()).thenReturn("Test Name");
        assertEquals("Test Name", appointmentType.getName());
    }

    @Test
    void testSetDescription() {
        appointmentType.setDescription("New Description");
        verify(mockDetails).setDescription("New Description");
    }

    @Test
    void testGetDescription() {
        when(mockDetails.getDescription()).thenReturn("Test Description");
        assertEquals("Test Description", appointmentType.getDescription());
    }

    @Test
    void testSetCategory() {
        List<String> categories = List.of("Health", "Wellness");
        appointmentType.setCategory(categories);
        verify(mockDetails).setCategory(categories);
    }

    @Test
    void testGetCategory() {
        List<String> categories = List.of("Health", "Wellness");
        when(mockDetails.getCategory()).thenReturn(categories);
        assertEquals(categories, appointmentType.getCategory());
    }

    @Test
    void testSetPrice() {
        appointmentType.setPrice(200.0);
        verify(mockDetails).setPrice(200.0);
    }

    @Test
    void testGetPrice() {
        when(mockDetails.getPrice()).thenReturn(150.0);
        assertEquals(150.0, appointmentType.getPrice());
    }

    @Test
    void testSetAppointmentDate() {
        LocalDate date = LocalDate.now().plusDays(5);
        appointmentType.setAppointmentDate(date);
        verify(mockDetails).setAppointmentDate(date);
    }

    @Test
    void testGetAppointmentDate() {
        LocalDate date = LocalDate.now().plusDays(5);
        when(mockDetails.getAppointmentDate()).thenReturn(date);
        assertEquals(date, appointmentType.getAppointmentDate());
    }

    @Test
    void testSetRequiredDocumentation() {
        List<String> docs = List.of("ID", "Insurance");
        appointmentType.setRequiredDocumentation(docs);
        verify(mockDetails).setRequiredDocumentation(docs);
    }

    @Test
    void testGetRequiredDocumentation() {
        List<String> docs = List.of("ID", "Insurance");
        when(mockDetails.getRequiredDocumentation()).thenReturn(docs);
        assertEquals(docs, appointmentType.getRequiredDocumentation());
    }

    @Test
    void testGetAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment());
        appointmentType.setAppointments(appointments);
        assertEquals(appointments, appointmentType.getAppointments());
    }

    @Test
    void testGetEvaluations() {
        List<Evaluation> evaluations = new ArrayList<>();
        evaluations.add(new Evaluation());
        appointmentType.setEvaluations(evaluations);
        assertEquals(evaluations, appointmentType.getEvaluations());
    }
}
