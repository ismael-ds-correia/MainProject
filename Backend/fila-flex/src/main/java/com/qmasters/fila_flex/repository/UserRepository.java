package com.qmasters.fila_flex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   UserDetails findByEmail(String email);
}
