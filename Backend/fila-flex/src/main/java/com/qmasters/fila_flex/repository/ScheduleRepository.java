package com.qmasters.fila_flex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.qmasters.fila_flex.model.Schedule;
import org.springframework.stereotype.Repository;


@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}
