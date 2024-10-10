package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.entities.Input;
import com.example.generateurformulaire.entities.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionServiceI {
    List<Question> getAllQuestions();

    Optional<Question> getQuestionById(Long id);

    Question createQuestion(Question question);


    Question updateQuestion(Long questionId, Question updatedQuestion);

    void deleteQuestionById(Long id);

    Question createBlankQuestion(Long stepId);

    List<Question> getQuestionsByStepId(Long stepId);

    Question assignInputToQuestion(Long questionId, Long inputId);

    Long getInputIdForQuestion(Long questionId);

    Input getInputByQuestionId(Long questionId);
}
