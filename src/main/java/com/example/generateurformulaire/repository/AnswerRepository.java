package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.DTO.AnswerDto;
import com.example.generateurformulaire.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a FROM Answer a WHERE a.submission.idSubmission = :submissionId")
    List<Answer> findBySubmissionId(Long submissionId);

    @Query("SELECT a FROM Answer a JOIN FETCH a.question WHERE a.submission.idSubmission = :submissionId")
    List<Answer> findAnswersWithQuestionsBySubmissionId(@Param("submissionId") Long submissionId);
    @Query("SELECT a FROM Answer a WHERE a.question.idQuestion = :questionId")

    List<Answer> findByQuestionId(Long questionId);


}


