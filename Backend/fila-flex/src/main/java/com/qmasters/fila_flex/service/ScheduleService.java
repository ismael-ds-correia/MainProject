package com.qmasters.fila_flex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.ScheduleDTO;
import com.qmasters.fila_flex.model.Schedule;
import com.qmasters.fila_flex.repository.ScheduleRepository;

import jakarta.transaction.Transactional;

@Service
public class ScheduleService {
    
    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Transactional
	public Schedule saveSchedule(ScheduleDTO scheduleDTO) {
		Schedule schedule = new Schedule(scheduleDTO.getAppointment(), scheduleDTO.getUser(), scheduleDTO.getScheduledDate());
		return scheduleRepository.save(schedule);
	}

    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    @Transactional
    public  ResponseEntity<?>  deleteSchedule(Long id) {
        try {
            scheduleRepository.deleteById(id);
            return ResponseEntity.ok("Schedule removido com sucesso");
        } catch (Exception e) {            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public Schedule update(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }
}