    package com.example.generateurformulaire.entities;

    import com.example.generateurformulaire.AppUser.User;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;
    @Entity
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public class Comment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idComment;

        @ManyToOne
        @JoinColumn(name = "form_id")
        private Form form;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        private String commentText;
        private LocalDateTime timestamp;
    }
