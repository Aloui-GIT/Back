package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "SELECT * FROM comment WHERE form_id = :formId", nativeQuery = true)
    List<Comment> findByFormId(@Param("formId") Long formId);
}

