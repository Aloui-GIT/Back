package com.example.generateurformulaire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Step {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idStep;
    private String title ;
    private int StepOrder ;
    private String StepDescription ;

    @JsonIgnore
    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();



    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "form_id") // Adjust name if needed
    private Form form;


    public Long getId() {
        return idStep ;
    }

    @Override
    public String toString() {
        return "Step{id=" + idStep + ", title='" + title + "'}";
    }


}
