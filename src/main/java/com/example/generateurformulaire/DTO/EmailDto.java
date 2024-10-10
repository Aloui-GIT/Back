package com.example.generateurformulaire.DTO;

import java.util.List;

public class EmailDto {
    private List<Long> userIds; // IDs of selected users
    private String subject;      // Subject of the email
    private String body;         // Body of the email

    // Getters and setters
    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}



