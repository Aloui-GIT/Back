package com.example.generateurformulaire.AppUser;

import com.example.generateurformulaire.entities.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "users")
public class User implements Serializable {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    private String verificationToken;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    String username;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "phone_number", length = 15)  // Adjust length as needed
    String phoneNumber;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "isLocked", nullable = false)
    boolean isLocked;

    @Column(name = "loginAttempts", nullable = false)
    int loginAttempts;

    public void setProfilPic(String profilPic) {
        this.profilPic = profilPic;
    }

    private String profilPic;

    @JsonIgnore
    @OneToOne
    Media profilPicture;



    @Transient
    String accessToken;

    @Transient
    String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    AppUserRole role;

    @ElementCollection(targetClass = AdminPermission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "admin_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<AdminPermission> adminPermissions = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Form> forms;
    @JsonIgnore
    @OneToMany(mappedBy = "user" )
    private List<Submission> submissions;

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username + '\'' + '}';
    }

    public User(Long userId) {
        this.userId = userId;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeDislike> likeDislikes = new ArrayList<>();
}
