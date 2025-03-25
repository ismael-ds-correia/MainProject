package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AppointmentTypeDetailsDTO;
import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.repository.AppointmentTypeDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class AppointmentTypeDetailsService {

    private final AppointmentTypeDetailsRepository appointmentTypeDetailsRepository;

    private final CategoryService categoryService;

    public AppointmentTypeDetailsService(AppointmentTypeDetailsRepository appointmentTypeDetailsRepository, CategoryService categoryService) {
        this.appointmentTypeDetailsRepository = appointmentTypeDetailsRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public AppointmentTypeDetails saveAppointmentTypeDetails(AppointmentTypeDetailsDTO dto) {
        AppointmentTypeDetails appointmentTypeDetails = new AppointmentTypeDetails(
            dto.getName(),
            dto.getDescription(),
            dto.getCategory(),
            dto.getPrice(),
            dto.getAppointmentDate(),
            dto.getRequiredDocumentation()
        );

        //Salva as categorias no banco de dados, se necessário.
        for (String categoryName : dto.getCategory()) {
            CategoryDTO categoryDTO = new CategoryDTO(categoryName);
            categoryService.saveCategory(categoryDTO);
        }
            
        return appointmentTypeDetailsRepository.save(appointmentTypeDetails);
    }

    public List<AppointmentTypeDetails> listAll() {
        return appointmentTypeDetailsRepository.findAll();
    }

    public Optional<AppointmentTypeDetails> findById(Long id) {
        return appointmentTypeDetailsRepository.findById(id);
    }

    @Transactional
    public void deleteAppointmentTypeDetails(Long id) {
        if (appointmentTypeDetailsRepository.existsById(id)) {
            appointmentTypeDetailsRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("AppointmentTypeDetails not found");
        }
    }

}
