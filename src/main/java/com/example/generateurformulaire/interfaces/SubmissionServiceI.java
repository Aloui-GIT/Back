package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.entities.Answer;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.Submission;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubmissionServiceI {
    List<Submission> getAllSubmissions();

    /*Optional<Submission> getSubmissionById(Long id);

    Submission createSubmission(Submission submission);

    Submission updateSubmission(Submission submission);

    void deleteSubmissionById(Long id);*/


    boolean validateUserAndFormExist(Long userId, Long formId);

    Submission createSubmission(Long userId, Long formId);


    Set<Form> getFormsBySubmissionId(Long submissionId);
}
