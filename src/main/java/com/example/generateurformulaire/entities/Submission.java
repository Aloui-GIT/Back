package com.example.generateurformulaire.entities;

import com.example.generateurformulaire.AppUser.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idSubmission;
    private Date DateSubmission ;

    private Long submissionCount;

    @Temporal(TemporalType.TIMESTAMP)
    private int timeSpent;  // When the user started the form

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;  // When the user started the form

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;    // When the user completed the form

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "form_id", nullable = true)
    private Form form;

    @JsonIgnore
    @OneToMany(mappedBy = "submission" , cascade = CascadeType.ALL)
    private List<Answer> answers;

    @Override
    public String toString() {
        return "Submission{id=" + idSubmission + ", date=" + DateSubmission + '}';
    }
}
