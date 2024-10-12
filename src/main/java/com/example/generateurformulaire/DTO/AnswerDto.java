package com.example.generateurformulaire.DTO;

import java.util.List;

public class AnswerDto {
    private Long questionId;
    private Long optionId;
    private String Answer ;
    private Long submissionId;


    // Getters and setters


    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }
}
