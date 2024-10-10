package com.example.generateurformulaire.services;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.DTO.AnswerDto;
import com.example.generateurformulaire.DTO.SubmissionDto;
import com.example.generateurformulaire.DTO.SubmissionSummary;
import com.example.generateurformulaire.entities.*;
import com.example.generateurformulaire.interfaces.SubmissionServiceI;
import com.example.generateurformulaire.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubmissionService implements SubmissionServiceI {

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private OptionsRepository optionsRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;


    @Override
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Override
    public boolean validateUserAndFormExist(Long userId, Long formId) {
        boolean userExists = userRepository.existsById(userId);
        boolean formExists = formRepository.existsById(formId);
        return userExists && formExists;
    }
    @Override
    public Submission createSubmission(Long userId, Long formId) {
        // Retrieve User and Form entities using their IDs
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new IllegalArgumentException("Form not found with id: " + formId));

        // Create a new Submission and set the current date
        Submission submission = new Submission();
        submission.setUser(user);
        submission.setForm(form);
        submission.setDateSubmission(new Date()); // Set to current date

        // Save the submission
        return submissionRepository.save(submission);
}

    public void saveSubmission(SubmissionDto submissionDto) {
        Submission submission = new Submission();
        submission.setDateSubmission(new Date());
        submission.setTimeSpent(submissionDto.getTimeSpent());

        // Set the user
        User user = userRepository.findById(submissionDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        submission.setUser(user);

        // Set the form
        Form form = formRepository.findById(submissionDto.getFormId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid form ID"));
        submission.setForm(form);

        // Convert AnswerDto to Answer entity
        List<Answer> answers = submissionDto.getAnswers().stream()
                .map(answerDto -> {
                    Answer answer = new Answer();
                    answer.setAnswer(answerDto.getAnswer()); // Set text answer if applicable
                    answer.setSubmission(submission);

                    // Set the question
                    Question question = questionRepository.findById(answerDto.getQuestionId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));
                    answer.setQuestion(question);

                    // If an option was selected, link it to the answer
                    if (answerDto.getOptionId() != null) {
                        Options option = optionsRepository.findById(answerDto.getOptionId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid option ID"));

                        // Validate that the option belongs to the question
                        if (!option.getQuestion().getIdQuestion().equals(answerDto.getQuestionId())) {
                            throw new IllegalArgumentException("Option does not belong to the specified question");
                        }

                        answer.setOption(option);
                    }

                    return answer;

                }).collect(Collectors.toList());
        submissionDto.getAnswers().forEach(answer -> log.info("Processing answer: " + answer));

        submission.setAnswers(answers);

        // Persist the submission along with its answers
        submissionRepository.save(submission);

        // Logging for debugging
        System.out.println("Submission saved with ID: " + submission.getIdSubmission());
        for (Answer answer : answers) {
            System.out.println("Answer saved: " + answer);
        }
    }


    public Optional<Submission> getSubmissionByUserIdAndFormId(Long userId, Long formId) {
        return submissionRepository.findByUserIdAndFormId(userId, formId);
    }

    public Submission updateSubmission(Long submissionId, SubmissionDto submissionDto) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        List<Answer> updatedAnswers = new ArrayList<>();
        for (AnswerDto answerDto : submissionDto.getAnswers()) {
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setAnswer(answerDto.getAnswer());
            answer.setSubmission(submission);

            updatedAnswers.add(answer);
        }

        submission.getAnswers().clear();
        submission.getAnswers().addAll(updatedAnswers);
        return submissionRepository.save(submission);
    }
    @Override
    public Set<Form> getFormsBySubmissionId(Long submissionId) {
        return submissionRepository.findFormsBySubmissionId(submissionId);
    }

    public List<Submission> getSubmissionsByFormId(Long formId) {
        return submissionRepository.findByFormId(formId);
    }


    public long getTotalSubmissions(Long formId) {
        return submissionRepository.countSubmissionsByFormId(formId);
    }

    public double getAverageResponseTime(Long formId) {
        List<Submission> submissions = submissionRepository.findAllByFormId(formId);
        long totalResponseTime = 0;

        for (Submission submission : submissions) {
            if (submission.getStartTime() != null && submission.getEndTime() != null) {
                totalResponseTime += submission.getEndTime().getTime() - submission.getStartTime().getTime();
            }
        }

        return submissions.size() > 0 ? (double) totalResponseTime / submissions.size() : 0;
    }

    public double getCompletionRate(Long formId) {
        long totalSubmissions = getTotalSubmissions(formId);
        long completedSubmissions = submissionRepository.countCompletedSubmissionsByFormId(formId);
        return (double) completedSubmissions / totalSubmissions * 100;
    }

    public List<Submission> getSubmissionHistoryByUserId(Long userId) {
        return submissionRepository.findByUserId(userId);
    }

    public List<SubmissionSummary> getSubmissionCounts() {
        List<Object[]> results = submissionRepository.countSubmissionsByForm();
        List<SubmissionSummary> submissionSummaries = new ArrayList<>();

        for (Object[] result : results) {
            Long formId = (Long) result[0]; // The first element is the form ID
            Long submissionCount = (Long) result[1]; // The second element is the count
            submissionSummaries.add(new SubmissionSummary(formId, submissionCount));
        }

        return submissionSummaries;
    }


    public int getSubmissionCountByUserAndForm(Long userId, Long formId) {
        return submissionRepository.countByUserIdAndFormId(userId, formId);
    }
}
