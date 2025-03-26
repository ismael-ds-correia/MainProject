package com.qmasters.fila_flex.controller;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.MetricsDTO;
import com.qmasters.fila_flex.service.AppointmentMetrics;

/**
 * Controller responsável por fornecer endpoints para obtenção de métricas
 * relacionadas a agendamentos de um tipo específico.
 */
@RestController
@RequestMapping("/metrics")
public class MetricsController {
    private final AppointmentMetrics appointmentMetrics;

    public MetricsController(AppointmentMetrics appointmentMetrics) {
        this.appointmentMetrics = appointmentMetrics;
    }

    @GetMapping("/appointment-type/{appointmentTypeName}")
    public ResponseEntity<MetricsDTO> getMetricsByAppointmentType(
            @PathVariable String appointmentTypeName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            MetricsDTO metrics = appointmentMetrics.generateMetrics(appointmentTypeName, startDate, endDate);
            return ResponseEntity.ok(metrics);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}