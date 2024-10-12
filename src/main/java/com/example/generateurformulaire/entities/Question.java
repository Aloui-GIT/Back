package com.example.generateurformulaire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idQuestion;
    private String Question ;
    private Boolean required ;

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }



    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "input_id")
    private Input input;

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Options> options;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;


    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @Override
    public String toString() {
        return "Question{id=" + idQuestion + ", Question='" + Question + "'}";
    }


}
