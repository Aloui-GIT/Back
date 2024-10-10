package com.example.generateurformulaire.DTO;

import java.util.Date;
import java.util.List;

public class SubmissionDto {
    private Long formId;
    private Long userId;
    private List<AnswerDto> answers;

    private int timeSpent;

    public Long getFormId() {
        return formId;
    }

    public Long getUserId() {
        return userId;
    }

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }
}
