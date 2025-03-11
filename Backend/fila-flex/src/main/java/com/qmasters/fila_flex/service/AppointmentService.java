package com.qmasters.fila_flex.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.dto.SimpleAppointmentDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.repository.AppointmentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Appointment saveAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = new Appointment(appointmentDTO.getAppointmentType()
        , appointmentDTO.getUser()
        , appointmentDTO.getScheduledDateTime());
        
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointment() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    //função para buscar Appointment por intervalo de datas.
    public List<SimpleAppointmentDTO> findByScheduledDateTime(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByScheduledDateTime(startDate, endDate).stream()
            .map(this::toSimpleDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca agendamentos pelo ID do usuário e os converte para SimpleAppointmentDTO
     * @param userId ID do usuário
     * @return Lista de agendamentos do usuário em formato SimpleAppointmentDTO
     */
    public List<SimpleAppointmentDTO> findAppointmentsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }
        return appointmentRepository.findByUserId(userId).stream()
            .map(this::toSimpleDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca agendamentos completos pelo ID do usuário
     * @param userId ID do usuário
     * @return Lista de agendamentos completos do usuário
     * @throws IllegalArgumentException se userId for nulo
     */
    public List<Appointment> findFullAppointmentsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }
        return appointmentRepository.findByUserId(userId);
    }

    //necessario para a função findByDateBetween / converte um tipo Appointment para SimpleAppointmentDTO
    private SimpleAppointmentDTO toSimpleDTO(Appointment appointment) {
        return new SimpleAppointmentDTO(
            appointment.getAppointmentType().getName(),
            appointment.getUser().getEmail(),
            appointment.getScheduledDateTime()
        );
    }
    
    @Transactional
    public Appointment updateAppointment(Long id, AppointmentDTO appointmentDto) {//não passei o AppointmentDTO como parametro, pois só preciso da data e hora
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            LocalDateTime now = LocalDateTime.now();

            if (appointment.getScheduledDateTime().isAfter(now.plusHours(12))) {//se o agendamento vai ocorrer em mais de 12 horas permite reagendar
                LocalDateTime createdDateTime = appointment.getCreatedDateTime();//mantem a data de criação original
                
                appointment.setScheduledDateTime(appointmentDto.getScheduledDateTime());
                appointment.setCreatedDateTime(createdDateTime);
                return appointmentRepository.save(appointment);

            } else {
                throw new IllegalArgumentException("Só é possivel reagendar uma consulta com pelomenos 12 horas de antecedencia.");
            }
        } else {
            throw new IllegalArgumentException("Agendamento não encontrado.");
        }
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            try {
                System.out.println("Iniciando exclusão do agendamento ID: " + id);
                
                //Usar uma consulta JPQL direta para forçar a exclusão
                int deletedCount = entityManager.createQuery(
                    "DELETE FROM Appointment a WHERE a.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
                
                System.out.println("Registros excluídos: " + deletedCount);
                
                //Força a sincronização com o banco de dados
                entityManager.flush();
                
                //Limpa o cache do JPA para garantir estado consistente
                entityManager.clear();
                
                //Verifica se a exclusão foi bem-sucedida
                boolean stillExists = appointmentRepository.existsById(id);
                if (stillExists) {
                    System.err.println("AVISO: Agendamento continua existindo após tentativa de exclusão");
                    throw new RuntimeException("Falha ao remover agendamento: ainda existe após exclusão");
                }
                
                System.out.println("Agendamento ID: " + id + " removido com sucesso");
            } catch (DataIntegrityViolationException e) {
                System.err.println("Erro de integridade de dados ao excluir agendamento: " + e.getMessage());
                throw new RuntimeException("Não foi possível excluir o agendamento devido a restrições de integridade. " +
                    "Verifique se há registros relacionados.", e);
            } catch (OptimisticLockingFailureException e) {
                System.err.println("Erro de concorrência ao excluir agendamento: " + e.getMessage());
                throw new RuntimeException("Erro de concorrência ao excluir o agendamento", e);
            } catch (IllegalArgumentException e) {
                System.err.println("Argumento inválido: " + e.getMessage());
                throw e;
            } catch (PersistenceException e) {
                System.err.println("Erro de persistência ao excluir agendamento: " + e.getMessage());
                throw new RuntimeException("Erro ao persistir a exclusão do agendamento. " + 
                    "Verifique a conexão com o banco de dados.", e);
            } catch (RuntimeException e) {
                System.err.println("Erro ao excluir agendamento: " + e.getMessage());
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Agendamento não encontrado, remoção não foi realizada");
        }
    }
}