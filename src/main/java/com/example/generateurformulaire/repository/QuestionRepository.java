package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.entities.Input;
import com.example.generateurformulaire.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.input WHERE q.step.idStep = :stepId")

    List<Question> findByStepId(Long stepId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.input WHERE q.step.form.idForm = :formId")
    List<Question> findQuestionsByFormId(Long formId);

}
