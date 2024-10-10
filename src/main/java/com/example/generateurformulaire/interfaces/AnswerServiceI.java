package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.entities.Answer;

import java.util.List;
import java.util.Optional;

public interface AnswerServiceI {
    List<Answer> getAllAnswers();

    Optional<Answer> getAnswerById(Long id);

    Answer createAnswer(Answer answer);

    Answer updateAnswer(Answer answer);

    void deleteAnswerById(Long id);

    List<Answer> createAnswers(List<Answer> answers);

    Answer addAnswerToSubmission(Long submissionId, Long questionId, String answerText);

    List<Answer> getAnswersBySubmissionId(Long submissionId);

    List<Answer> getAnswerssBySubmissionId(Long submissionId);
}
