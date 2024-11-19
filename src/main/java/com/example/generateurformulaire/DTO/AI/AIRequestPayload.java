package com.example.generateurformulaire.DTO.AI;

import java.util.List;

public class AIRequestPayload {

    private String title;
    private String description;
    private List<String> questions;

    public AIRequestPayload(String title, String description, List<String> questions) {
        this.title = title;
        this.description = description;
        this.questions = questions;
    }

}
