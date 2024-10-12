package com.example.generateurformulaire.services;

import com.example.generateurformulaire.DTO.AnswerDto;
import com.example.generateurformulaire.entities.Answer;
import com.example.generateurformulaire.entities.Options;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.entities.Submission;
import com.example.generateurformulaire.interfaces.AnswerServiceI;
import com.example.generateurformulaire.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AnswerService implements AnswerServiceI {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private  AnswerRepository answerRepository;
    @Autowired

    private OptionsRepository optionsRepository ;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private OptionsService optionService ;

    @Autowired
    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    @Override
    public Optional<Answer> getAnswerById(Long id) {
        return answerRepository.findById(id);
    }

    @Override
    public Answer createAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public Answer updateAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public void deleteAnswerById(Long id) {
        answerRepository.deleteById(id);
    }



    @Override
    public Answer addAnswerToSubmission(Long submissionId, Long questionId, String answerText) {
        // Retrieve Submission and Question entities using their IDs
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found with id: " + submissionId));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));

        // Create a new Answer and set its properties
        Answer answer = new Answer();
        answer.setAnswer(answerText); // Set the answer text
        answer.setSubmission(submission); // Link to the correct submission
        answer.setQuestion(question); // Link to the correct question

        return answerRepository.save(answer);
    }

    public Answer updateAnswer(Long id, String answerText) {
        // Retrieve the existing Answer entity
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with id: " + id));

        // Update the answer text if provided
        if (answerText != null) {
            answer.setAnswer(answerText);
        }

        // Save the updated answer
        return answerRepository.save(answer);
    }


    @Override
    public List<Answer> createAnswers(List<Answer> answers) {
        // Validate answers if necessary
        validateAnswers(answers);

        // Save answers to the repository
        return answerRepository.saveAll(answers);
    }

    private void validateAnswers(List<Answer> answers) {
        for (Answer answer : answers) {
            if (answer.getAnswer() == null || answer.getAnswer().isEmpty()) {
                throw new IllegalArgumentException("Answer text cannot be null or empty");
            }
            if (answer.getQuestion() == null || answer.getQuestion().getIdQuestion() == null) {
                throw new IllegalArgumentException("Question cannot be null and must have an ID");
            }
        }



    }





    public void assignOptionToAnswer(Long answerId, Long optionId) {
        // Find the Answer entity
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + answerId));

        // Find the Options entity
        Options option = optionsRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found with id: " + optionId));

        // Assign the option to the answer
        answer.setOption(option);

        // Save the updated answer
        answerRepository.save(answer);
    }
    @Override
    public List<Answer> getAnswersBySubmissionId(Long submissionId) {
        return answerRepository.findBySubmissionId(submissionId);
    }
    @Override
    public List<Answer> getAnswerssBySubmissionId(Long submissionId) {
        return answerRepository.findAnswersWithQuestionsBySubmissionId(submissionId);
    }

    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }
}