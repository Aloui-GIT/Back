package com.example.generateurformulaire.entities;

import com.example.generateurformulaire.AppUser.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Form {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idForm;
    private String title ;
    private String Description ;
    private Date CreateDate ;
    private Date lastModificationDate;
    private String screenshotPath;
    private boolean acceptingResponses;
    private boolean template;
    private int likesCount;  // Add this
    private int dislikesCount; // Add this
    private int maxSubmissionsPerUser;
    @JsonIgnore
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> steps = new ArrayList<>();


    @JsonIgnore
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeDislike> likeDislikes = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @JsonIgnore
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions;


    public int getMaxSubmissionsPerUser() {
        return maxSubmissionsPerUser;
    }

    public void setMaxSubmissionsPerUser(int maxSubmissionsPerUser) {
        this.maxSubmissionsPerUser = maxSubmissionsPerUser;
    }
}
