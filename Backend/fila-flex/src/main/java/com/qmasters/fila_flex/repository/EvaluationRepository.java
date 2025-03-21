package com.qmasters.fila_flex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.Evaluation;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}