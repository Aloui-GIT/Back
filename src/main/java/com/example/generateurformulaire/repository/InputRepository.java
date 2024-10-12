package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.entities.Input;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputRepository extends JpaRepository<Input, Long> {
}
