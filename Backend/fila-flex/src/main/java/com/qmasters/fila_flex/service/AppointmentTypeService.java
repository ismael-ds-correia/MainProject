package com.qmasters.fila_flex.service;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentTypeService {

    private final AppointmentTypeRepository repository;

    public AppointmentTypeService(AppointmentTypeRepository repository) {
        this.repository = repository;
    }

    public AppointmentTypeDTO criar(AppointmentTypeDTO dto) {
        AppointmentType appointmentType = new AppointmentType(
            null,
            dto.getNome(),
            dto.getDescricao(),
            dto.getCategoria(),
            dto.getPreco(),
            dto.getTempoExecucao(),
            dto.getDataEntrega(),
            dto.getDocumentacaoNecessaria()
        );

        appointmentType = repository.save(appointmentType);
        return toDTO(appointmentType);
    }

    public List<AppointmentTypeDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AppointmentTypeDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Tipo de agendamento não encontrado"));
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    private AppointmentTypeDTO toDTO(AppointmentType appointmentType) {
        return new AppointmentTypeDTO(
                appointmentType.getId(),
                appointmentType.getNome(),
                appointmentType.getDescricao(),
                appointmentType.getCategoria(),
                appointmentType.getPreco(),
                appointmentType.getTempoExecucao(),
                appointmentType.getDataEntrega(),
                appointmentType.getDocumentacaoNecessaria()
        );
    }
}
