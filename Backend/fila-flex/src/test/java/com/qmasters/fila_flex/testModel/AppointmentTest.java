package com.qmasters.fila_flex.testModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.model.User;

class AppointmentTest {

    private Appointment appointment;
    private AppointmentType appointmentType;
    private AppointmentTypeDetails details;
    private User user;
    private Adress address;

    @BeforeEach
    void setup() {
        // Configura os dados padrão
        details = new AppointmentTypeDetails();
        details.setName("Consultation");
        details.setDescription("Consultation with a doctor");
        details.setPrice(100.0);
        details.setCategory(List.of("General"));
        details.setRequiredDocumentation(List.of("ID"));
        details.setAppointmentDate(LocalDate.now().plusDays(2));

        appointmentType = new AppointmentType();
        appointmentType.setAppointmentTypeDetails(details);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        address = new Adress(); // Supondo construtor padrão
        appointmentType.setAdress(address);

        appointment = new Appointment(appointmentType, user, LocalDateTime.now().plusDays(1));
    }

    // Testes para getters com objeto preenchido
    @Test
    void testGetAppointmentTypeCategory_WhenDetailsExist() {
        List<String> category = appointment.getAppointmentTypeCategory();
        assertNotNull(category);
        assertEquals(List.of("General"), category);
    }

    @Test
    void testGetAppointmentTypeRequiredDocumentation_WhenDetailsExist() {
        List<String> docs = appointment.getAppointmentTypeRequiredDocumentation();
        assertNotNull(docs);
        assertEquals(List.of("ID"), docs);
    }

    @Test
    void testGetAppointmentTypePrice_WhenDetailsExist() {
        String price = appointment.getAppointmentTypePrice();
        assertNotNull(price);
        assertEquals("100.0", price);
    }

    @Test
    void testGetAppointmentTypeAppointmentDate_WhenDetailsExist() {
        String appDate = appointment.getAppointmentTypeAppointmentDate();
        assertNotNull(appDate);
        assertEquals(details.getAppointmentDate().toString(), appDate);
    }

    @Test
    void testGetAppointmentTypeDetailsName_WhenDetailsExist() {
        String name = appointment.getAppointmentTypeDetailsName();
        assertNotNull(name);
        assertEquals("Consultation", name);
    }

    @Test
    void testGetAppointmentTypeDescription_WhenDetailsExist() {
        String description = appointment.getAppointmentTypeDescription();
        assertNotNull(description);
        assertEquals("Consultation with a doctor", description);
    }

    @Test
    void testGetUserId_WhenUserExists() {
        String userId = appointment.getUserId();
        assertNotNull(userId);
        assertEquals("1", userId);
    }

    @Test
    void testGetUserEmail_WhenUserExistsButEmailIsNull() {
        appointment.getUser().setEmail(null);
        String email = appointment.getUserEmail();
        assertNull(email); // Deve retornar null pois o email do usuário é null
    }
    
    @Test
    void testGetUserId_WhenUserExistsButIdIsNull() {
        appointment.getUser().setId(null);
        String userId = appointment.getUserId();
        assertNull(userId); // Deve retornar null pois o ID do usuário é null
    }

    @Test
    void testGetAppointmentTypeAdress_WhenAdressExists() {
        Adress addr = appointment.getAppointmentTypeAdress();
        assertNotNull(addr);
        assertEquals(address, addr);
    }

    @Test
    void testGetUserEmail_WhenUserExists() {
        String email = appointment.getUserEmail();
        assertNotNull(email);
        assertEquals("test@example.com", email);
    }

    // Testes para os casos em que os objetos dependentes são nulos

    @Test
    void testGetAppointmentTypeCategory_WhenAppointmentTypeIsNull() {
        appointment.setAppointmentType(null);
        List<String> category = appointment.getAppointmentTypeCategory();
        assertNotNull(category);
        assertTrue(category.isEmpty());
    }

    @Test
    void testGetAppointmentTypeCategory_WhenDetailsAreNull() {
        appointmentType.setAppointmentTypeDetails(null);
        List<String> category = appointment.getAppointmentTypeCategory();
        assertNotNull(category);
        assertTrue(category.isEmpty());
    }

    @Test
    void testGetAppointmentTypeRequiredDocumentation_WhenAppointmentTypeIsNull() {
        appointment.setAppointmentType(null);
        List<String> docs = appointment.getAppointmentTypeRequiredDocumentation();
        assertNotNull(docs);
        assertTrue(docs.isEmpty());
    }

    @Test
    void testGetAppointmentTypeRequiredDocumentation_WhenDetailsAreNull() {
        appointmentType.setAppointmentTypeDetails(null);
        List<String> docs = appointment.getAppointmentTypeRequiredDocumentation();
        assertNotNull(docs);
        assertTrue(docs.isEmpty());
    }

    @Test
    void testGetAppointmentTypePrice_WhenAppointmentTypeIsNull() {
        appointment.setAppointmentType(null);
        String price = appointment.getAppointmentTypePrice();
        assertNull(price);
    }

    @Test
    void testGetAppointmentTypePrice_WhenDetailsAreNull() {
        appointmentType.setAppointmentTypeDetails(null);
        String price = appointment.getAppointmentTypePrice();
        assertNull(price);
    }

    @Test
    void testGetAppointmentTypeAppointmentDate_WhenAppointmentTypeIsNull() {
        appointment.setAppointmentType(null);
        String date = appointment.getAppointmentTypeAppointmentDate();
        assertNull(date);
    }

    @Test
    void testGetAppointmentTypeAppointmentDate_WhenDetailsAreNull() {
        appointmentType.setAppointmentTypeDetails(null);
        String date = appointment.getAppointmentTypeAppointmentDate();
        assertNull(date);
    }

    @Test
    void testGetAppointmentTypeDetailsName_WhenAppointmentTypeIsNull() {
        appointment.setAppointmentType(null);
        String name = appointment.getAppointmentTypeDetailsName();
        assertNull(name);
    }

    @Test
    void testGetAppointmentTypeDetailsName_WhenDetailsAreNull() {
        appointmentType.setAppointmentTypeDetails(null);
        String name = appointment.getAppointmentTypeDetailsName();
        assertNull(name);
    }

    @Test
    void testGetAppointmentTypeDescription_WhenAppointmentTypeIsNull() {
        appointment.setAppointmentType(null);
        String description = appointment.getAppointmentTypeDescription();
        assertNull(description);
    }

    @Test
    void testGetAppointmentTypeDescription_WhenDetailsAreNull() {
        appointmentType.setAppointmentTypeDetails(null);
        String description = appointment.getAppointmentTypeDescription();
        assertNull(description);
    }

    @Test
    void testGetUserId_WhenUserIsNull() {
        appointment.setUser(null);
        String userId = appointment.getUserId();
        assertNull(userId);
    }

    @Test
    void testGetAppointmentTypeAdress_WhenAppointmentTypeIsNull() {
        appointment.setAppointmentType(null);
        Adress addr = appointment.getAppointmentTypeAdress();
        assertNull(addr);
    }

    @Test
    void testGetAppointmentTypeAdress_WhenAdressIsNull() {
        appointmentType.setAdress(null);
        Adress addr = appointment.getAppointmentTypeAdress();
        assertNull(addr);
    }

    @Test
    void testGetUserEmail_WhenUserIsNull() {
        appointment.setUser(null);
        String email = appointment.getUserEmail();
        assertNull(email);
    }

    // Testes para os setters
    @Test
    void testSetAppointmentType() {
        AppointmentType newType = new AppointmentType();
        newType.setAppointmentTypeDetails(details);
        appointment.setAppointmentType(newType);
        assertEquals(newType, appointment.getAppointmentType());
    }

    @Test
    void testSetUser() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("new@example.com");
        appointment.setUser(newUser);
        assertEquals(newUser, appointment.getUser());
    }
}