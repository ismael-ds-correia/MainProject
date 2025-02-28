package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;

@Service
public class AppointmentTypeService {
    @Autowired
    private AppointmentTypeRepository repository;

    public AppointmentTypeDTO toCreate(AppointmentTypeDTO dto) {
        AppointmentType appointmentType = new AppointmentType(
                dto.getName(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getPrice(),
                dto.getRuntime(),
                dto.getEstimatedTime(),
                dto.getRequiredDocumentation()
         );

        appointmentType = repository.save(appointmentType);

        return toDTO(appointmentType);
    }

    public List<AppointmentTypeDTO> listAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AppointmentTypeDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Tipo de agendamento não encontrado."));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private AppointmentTypeDTO toDTO(AppointmentType appointmentType) {
        return new AppointmentTypeDTO(appointmentType.getName(),
                appointmentType.getDescription(),
                appointmentType.getCategory(),
                appointmentType.getPrice(),
                appointmentType.getRuntime(),
                appointmentType.getEstimatedTime(),
                appointmentType.getRequiredDocumentation()
        );
    }
}