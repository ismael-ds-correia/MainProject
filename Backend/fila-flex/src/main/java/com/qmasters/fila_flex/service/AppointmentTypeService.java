package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;

@Service
public class AppointmentTypeService {

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;
    
    @Autowired
    private CategoryService categoryService;     //Importante para salvar categorias no banco de dados.

    public AppointmentTypeDTO saveAppointmentType(AppointmentTypeDTO dto) {
        AppointmentType appointmentType = new AppointmentType(
                dto.getName(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getPrice(),
                dto.getEstimatedTime(),
                dto.getAppointmentDate(),
                dto.getRequiredDocumentation(),
                dto.getAdress()
         );

        appointmentType = appointmentTypeRepository.save(appointmentType);

        //Salva as categorias no banco de dados, se necessário.
        for (String categoryName : dto.getCategory()) {
            CategoryDTO categoryDTO = new CategoryDTO(categoryName);
            categoryService.saveCategory(categoryDTO);
        }

        return toDTO(appointmentType);
    }

    public List<AppointmentType> listAll() {
        return appointmentTypeRepository.findAll();
    }

    public AppointmentType findById(Long id) {
        return appointmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppointmentType não encontrado."));
    }
/* 
    public AppointmentTypeDTO findById(Long id) {
        return appointmentTypeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("AppointmentType não encontrado."));
    }*/

    //Método para buscar AppointmentTypes filtrados por categoria.
    public List<AppointmentTypeDTO> findByCategory(String category) {
        return appointmentTypeRepository.findByCategory(category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //Método para buscar AppointmentTypes por intervalo de preços.
    public List<AppointmentTypeDTO> findByPriceBetween(double minPrice, double maxPrice) {
        return appointmentTypeRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteAppointmentType(Long id) {
        appointmentTypeRepository.deleteById(id);
    }

    //Método para deletar AppointmentType por nome.
    public void deleteByName(String name) {
        // Encontra o AppointmentType pelo nome
        AppointmentType appointmentType = appointmentTypeRepository.findByName(name)
            .orElseThrow(() -> new RuntimeException("Tipo de agendamento não encontrado com o nome: " + name));
        
        // Deleta o AppointmentType
        appointmentTypeRepository.delete(appointmentType);
    }

    public AppointmentType findByName(String name) {
        return appointmentTypeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("AppointmentType não encontrado."));
    }

    private AppointmentTypeDTO toDTO(AppointmentType appointmentType) {
        return new AppointmentTypeDTO(appointmentType.getName(),
                appointmentType.getDescription(),
                appointmentType.getCategory(),
                appointmentType.getPrice(),
                appointmentType.getEstimatedTime(),
                appointmentType.getAppointmentDate(),
                appointmentType.getRequiredDocumentation(),
                appointmentType.getAdress()
        );
    }
}