package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.entities.Options;
import com.example.generateurformulaire.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {
    @Query("SELECT o FROM Options o WHERE o.question.idQuestion = :questionId")
    List<Options> findByQuestionId(@Param("questionId") Long questionId);
}
