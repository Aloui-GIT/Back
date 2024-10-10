package com.example.generateurformulaire.repository;


import com.example.generateurformulaire.entities.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {
    @Query("SELECT s FROM Step s WHERE s.form.idForm = :formId")

    List<Step> findByFormId(Long formId);


    @Query("SELECT COUNT(s) FROM Step s WHERE s.form.id = :formId")
    long countStepsByFormId(@Param("formId") Long formId);
}
