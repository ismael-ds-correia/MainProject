package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.dto.CategoryDTO;
import com.qmasters.fila_flex.exception.InvalidPriceRangeException;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;

import jakarta.transaction.Transactional;

@Service
public class AppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;
    
    private final CategoryService categoryService;     //Importante para salvar categorias no banco de dados.

    public AppointmentTypeService(AppointmentTypeRepository appointmentTypeRepository, CategoryService categoryService) {
        this.appointmentTypeRepository = appointmentTypeRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public AppointmentType saveAppointmentType(AppointmentTypeDTO dto) {
        AppointmentType appointmentType = new AppointmentType(
            dto.getAppointmentTypeDetails(),
            dto.getEstimatedTime(),
            dto.getAdress()
        );

         //Salva as categorias no banco de dados, se necessário.
        for (String categoryName : dto.getAppointmentTypeDetails().getCategory()) {
            CategoryDTO categoryDTO = new CategoryDTO(categoryName);
            categoryService.saveCategory(categoryDTO);
        }
            
        return appointmentTypeRepository.save(appointmentType);
    }

    public List<AppointmentType> listAll() {
        return appointmentTypeRepository.findAll();
    }

    public Optional<AppointmentType> findById(Long id) {
        return appointmentTypeRepository.findById(id);
     }
    
    public Optional<AppointmentType> findByName(String name) {//talvez precise criar função manualmente no repository
        return appointmentTypeRepository.findByName(name);
    }

    // buscar AppointmentTypes filtrados por categoria.
    public List<AppointmentType> findByCategory(String category) {//talvez precise criar função manualmente no repository
        return appointmentTypeRepository.findByCategory(category);
    }

    public List<AppointmentType> findAllByOrderByEstimatedTimeAsc() {
        return appointmentTypeRepository.findAllByOrderByEstimatedTimeAsc();
    }
    
    //buscar AppointmentTypes por intervalo de preços.
    public List<AppointmentType> findByPriceBetween(double minPrice, double maxPrice) {
        if (minPrice > maxPrice) {
            throw new InvalidPriceRangeException("Preço mínimo não pode ser maior que o preço máximo.");
        } else {
            return appointmentTypeRepository.findByPriceBetween(minPrice, maxPrice);
        }
    }
    
    //buscar AppointmentTypes por ordem de tempo estimado.
    
    @Transactional
    public void deleteByName(String name) {
        var appointmentType = appointmentTypeRepository.findByName(name);
        if (appointmentType.isEmpty()) {
            throw new NoSuchElementException("Tipo de agendamento não encontrado.");
        } else {
            appointmentTypeRepository.delete(appointmentType.get());
        }

    }
    
    @Transactional
    public void deleteAppointmentType(Long id) {
        if (appointmentTypeRepository.existsById(id)) {
            appointmentTypeRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("AppointmentType não encontrado. Remoção não realizada.");
        }
    }

}