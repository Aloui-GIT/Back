package com.example.generateurformulaire.DTO;

public class SubmissionSummary {
    private Long formId;
    private Long submissionCount;

    public SubmissionSummary(Long formId, Long submissionCount) {
        this.formId = formId;
        this.submissionCount = submissionCount;
    }
}
