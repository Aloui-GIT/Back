package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Input;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.entities.Step;
import com.example.generateurformulaire.interfaces.QuestionServiceI;
import com.example.generateurformulaire.repository.InputRepository;
import com.example.generateurformulaire.repository.QuestionRepository;
import com.example.generateurformulaire.repository.StepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestionService implements QuestionServiceI {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private StepRepository stepRepository ;
    @Autowired
    private InputRepository inputRepository ;

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }


    @Override
    public Question updateQuestion(Long questionId, Question updatedQuestion) {
        // Check if the question exists
        Optional<Question> existingQuestionOptional = questionRepository.findById(questionId);
        if (existingQuestionOptional.isEmpty()) {
            throw new EntityNotFoundException("Question with ID " + questionId + " not found.");
        }

        // Retrieve the existing question
        Question existingQuestion = existingQuestionOptional.get();

        // Update the existing question with new data
        existingQuestion.setQuestion(updatedQuestion.getQuestion()); // Update the question text
        existingQuestion.setRequired(updatedQuestion.getRequired()); // Update 'required' attribute

        // If the input is provided, update the input relationship
        if (updatedQuestion.getInput() != null) {
            Input input = inputRepository.findById(updatedQuestion.getInput().getIdInput())
                    .orElseThrow(() -> new EntityNotFoundException("Input with ID " + updatedQuestion.getInput().getIdInput() + " not found."));
            existingQuestion.setInput(input);
        }

        // Save and return the updated question
        return questionRepository.save(existingQuestion);
    }



    @Override
    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public Question createBlankQuestion(Long stepId) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));

        Question question = new Question();
        question.setQuestion("Default Question"); // Set default question text
        question.setStep(step); // Associate question with the step

        // Add the question to the step's collection (optional if cascade is set)
        // step.getQuestions().add(question);

        // Save the question
        Question savedQuestion = questionRepository.save(question);

        return savedQuestion;
    }
    @Override
    public List<Question> getQuestionsByStepId(Long stepId) {
        return questionRepository.findByStepId(stepId);
    }
    @Override
    public Question assignInputToQuestion(Long questionId, Long inputId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        Input input = inputRepository.findById(inputId)
                .orElseThrow(() -> new ResourceNotFoundException("Input not found"));
        question.setInput(input);
        return questionRepository.save(question);
    }

    @Override
    public Long getInputIdForQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    Input input = question.getInput();
                    return input != null ? input.getIdInput() : null;
                })
                .orElse(null);
    }

    @Override
    public Input getInputByQuestionId(Long questionId) {
        return questionRepository.findById(questionId)
                .map(Question::getInput)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }

    public List<Question> getQuestionsByFormId(Long formId) {
        return questionRepository.findQuestionsByFormId(formId);
    }

    public boolean isQuestionRequired(Long idQuestion) {
        return questionRepository.findById(idQuestion)
                .map(Question::getRequired)  // Use getter to obtain the 'required' value
                .orElse(false);  // Default to false if the question is not found
    }

    public boolean isOptionsRequired(Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isPresent()) {
            Long inputId = optionalQuestion.get().getInput().getIdInput();
            // Check directly using hard-coded IDs
            return inputId != null && (inputId == 3L || inputId == 4L || inputId == 5L);
        }
        return false;
    }


}