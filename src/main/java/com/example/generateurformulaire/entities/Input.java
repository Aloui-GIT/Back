package com.example.generateurformulaire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Input {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idInput;
    private String InputType ;




    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "input", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions ;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "input_id")
    private List<Options> options;



}
