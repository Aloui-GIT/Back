package com.example.generateurformulaire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Options {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idOption;
    private String option ;
    private String OptionValue ;
    private int OptionOrder ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;



}
