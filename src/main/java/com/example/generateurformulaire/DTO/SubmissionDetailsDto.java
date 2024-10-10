package com.example.generateurformulaire.DTO;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.entities.Answer;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.Question;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SubmissionDetailsDto {
    private Long idSubmission;
    private Date dateSubmission;
    private User user;
    private Form form;
    private List<Answer> answers;
    private List<Question> questions; // Add this field


    public Long getIdSubmission() {
        return idSubmission;
    }

    public void setIdSubmission(Long idSubmission) {
        this.idSubmission = idSubmission;
    }

    public Date getDateSubmission() {
        return dateSubmission;
    }

    public void setDateSubmission(Date dateSubmission) {
        this.dateSubmission = dateSubmission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
