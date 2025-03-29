package com.qmasters.fila_flex.testModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qmasters.fila_flex.model.AppointmentTypeDetails;

class AppointmentTypeDetailsTest {
    private AppointmentTypeDetails details;

    @BeforeEach
    void setup() {
        details = new AppointmentTypeDetails("Consultation", "Consultation with a doctor", 
                new ArrayList<>(List.of("General")), 100.0, LocalDate.now().plusDays(2),
                new ArrayList<>(List.of("ID")));
    }

    @Test
    void testToString() {
        String result = details.toString();
        assertNotNull(result);
        assertTrue(result.contains("Consultation"));
        assertTrue(result.contains("Consultation with a doctor"));
        assertTrue(result.contains("100.0"));
        assertTrue(result.contains("General"));
        assertTrue(result.contains("ID"));
        assertTrue(result.contains(details.getAppointmentDate().toString()));
    }

    @Test
    void testAddCategory_WhenCategoryListIsEmpty() {
        details.setCategory(new ArrayList<>());
        details.addCategory("New Category");
        assertEquals(1, details.getCategory().size());
        assertEquals("New Category", details.getCategory().get(0));
    }

    @Test
    void testAddCategory_WhenCategoryListIsNotEmpty() {
        details.addCategory("New Category");
        assertEquals(2, details.getCategory().size());
        assertTrue(details.getCategory().contains("New Category"));
    }

    @Test
    void testAddCategory_WhenAddingMultipleCategories() {
        details.addCategory("Category 1");
        details.addCategory("Category 2");
        assertEquals(3, details.getCategory().size());
        assertTrue(details.getCategory().contains("Category 1"));
        assertTrue(details.getCategory().contains("Category 2"));
    }

    @Test
    void testAddCategory_WithNullParameter_WhenCategoryListIsNull() {
        details.setCategory(null);  // força a lista a ser nula
        details.addCategory(null);
        assertNotNull(details.getCategory());
        assertEquals(1, details.getCategory().size());
        assertNull(details.getCategory().get(0));
    }

    @Test
    void testAddCategory_WithNullParameter_WhenCategoryListIsNotNull() {
        // A lista já vem preenchida pelo setup
        details.addCategory(null);
        assertEquals(2, details.getCategory().size());
        assertTrue(details.getCategory().contains(null));
    }
}